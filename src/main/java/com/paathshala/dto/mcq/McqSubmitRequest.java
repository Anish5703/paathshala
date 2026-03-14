package com.paathshala.dto.mcq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class McqSubmitRequest{

   private List<McqAttempt> attempts;
}
