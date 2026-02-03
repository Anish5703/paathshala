package com.paathshala.dto;

import com.paathshala.model.ErrorType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
   private ErrorType errorType;
   private  String message;



}
