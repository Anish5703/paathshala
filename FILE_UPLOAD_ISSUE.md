# File Upload Issue Analysis: Course Images Not Persisting

## Problem Statement

Course images appear to be saved successfully (logs show "Image saved successfully"), but the actual image files are not persisting in `/uploads/images`, while notes are being saved successfully in `/uploads/notes` using the same approach.

## Root Cause

The issue is **NOT** with the file saving logic—both use identical code patterns. The problem is with **Docker volume mount configuration**:

### Missing Volume Mount for Course Images

In `docker-compose.yml`, the `/uploads/images` directory is **NOT mounted as a Docker volume**:

```yaml
# Current Configuration (INCOMPLETE)
volumes:
  - ./uploads/notes:/uploads/notes           # ✅ Persists to host
  - ./uploads/videos:/uploads/videos         # ✅ Persists to host
  - ./uploads/modelQuestions:/uploads/modelQuestions  # ✅ Persists to host
  # ❌ MISSING: ./uploads/images:/uploads/images
```

**Consequence:**
- Course images are saved **inside the Docker container** at `/uploads/images`
- Without a volume mount, these files are **lost when the container restarts or is recreated**
- The logs show success because the file write operation succeeds **within the container**
- But the files never reach the host filesystem

## Why Notes Work but Images Don't

Both notes and images use identical file saving logic in their respective services:

| Aspect | Notes (ContentService) | Images (CourseService) |
|--------|------------------------|------------------------|
| **File Saving Method** | `Files.write()` with UUID naming | `Files.write()` with UUID naming |
| **Directory Creation** | `Files.createDirectories()` if not exists | `Files.createDirectories()` if not exists |
| **Upload Directory** | `/uploads/notes` (line 41) | `/uploads/images` (line 42) |
| **Configuration** | `note.dir=/uploads/notes` in application.properties | `course.dir=/uploads/images` in application.properties |
| **Difference** | **MOUNTED as Docker volume** | **NOT MOUNTED as Docker volume** |

**The logic is identical; the persistence is not.**

## Technical Details

### File Upload Flow

```
1. CourseController receives multipart form with image
   ↓
2. CourseService.addCourse() → storeImage() method
   ↓
3. Resolves upload directory: /uploads/images
   ↓
4. Creates UUID + extension filename: c9a3bf95-2471-423b-a670-93b4bc151c9c.jpg
   ↓
5. Files.write() saves to: /uploads/images/c9a3bf95-2471-423b-a670-93b4bc151c9c.jpg
   ↓
6. Stores filename in database
   ↓
7. Log shows: "Image saved successfully at: /uploads/images/..."
   ↓
8. ⚠️ BUT: File only exists inside Docker container, not on host filesystem
```

### Configuration Evidence

**From application.properties:**
```properties
course.dir=/uploads/images
note.dir=/uploads/notes
```

**From CourseService.java (line 42):**
```java
@Value("${course.dir}")
private String imageUploadDir;
```

**From ContentService.java (line 41):**
```java
@Value("${note.dir}")
private String noteUploadDir;
```

**From docker-compose.yml (lines 54-59):**
```yaml
volumes:
  - ./uploads/notes:/uploads/notes           # Container → Host mapping
  - ./uploads/videos:/uploads/videos
  - ./uploads/modelQuestions:/uploads/modelQuestions
  # MISSING for images!
```

## Why You See Success Logs But No Files

The logs are reporting **success at the container level**:
- The file is successfully written to `/uploads/images/` **inside the container**
- The database insert succeeds
- The API returns a 200 response
- All appears normal to the user

However, without a volume mount, the container's internal filesystem is ephemeral:
- Files don't sync to the host `/uploads/images/` directory
- Container restart/restart = all files lost
- Next container startup has empty `/uploads/images/` directory

## Impact

### Current Behavior
- ✅ Notes: Persist to host filesystem (volume mounted)
- ❌ Images: Lost on container restart (no volume mount)
- ⚠️ Silent data loss—no errors in logs

### Real-World Scenario
1. Admin uploads course with image → File saved in container, visible in logs
2. Container crashes or is updated → Restarted
3. Course image reference in database still exists, but file is gone
4. Frontend tries to load image → 404 error
5. User sees broken image links

## Solution

Add the missing volume mount to `docker-compose.yml`:

```yaml
volumes:
  - ./uploads/notes:/uploads/notes
  - ./uploads/videos:/uploads/videos
  - ./uploads/modelQuestions:/uploads/modelQuestions
  - ./uploads/images:/uploads/images  # ← ADD THIS LINE
```

This ensures:
- Course images persist to host filesystem at `./uploads/images/`
- Images survive container restarts
- Consistent behavior with notes, videos, and model questions
- No data loss on container updates

## Verification After Fix

After adding the volume mount:

1. **Restart Docker:**
   ```bash
   docker-compose down && docker-compose up -d
   ```

2. **Create a new course with image**

3. **Verify file exists on host:**
   ```bash
   ls -la ./uploads/images/
   ```

4. **Stop and restart container:**
   ```bash
   docker-compose restart
   ```

5. **Verify images still exist and are accessible**

## Key Takeaway

| Aspect | Root Cause |
|--------|-----------|
| **Why logs show success?** | File write operation succeeds inside container |
| **Why file doesn't persist?** | No Docker volume mount for `/uploads/images` |
| **Why notes work?** | `/uploads/notes` IS volume mounted |
| **Why same code, different results?** | Volume mounting, not code logic |
| **Fix?** | Add `./uploads/images:/uploads/images` to docker-compose.yml volumes |
