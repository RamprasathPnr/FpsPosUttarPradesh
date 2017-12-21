package com.omneagate.DTO;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class FindingCriteriaDto {
    //Finding Criteria
    String criteria;
    /* //Status-subcriteria
      String status;

      String subCriteria;

     //Observation
      String observation;

     //Camera Photo path or attachment
      String pathPhotoCamera;*/
    //base64 format
    String photo;
    List<CardInspectionDto> cardInspection = new ArrayList<>();
    List<StockInspectionDto> stockInspection = new ArrayList<>();
    List<WeighmentInspectionDto> weighmentInspection = new ArrayList<>();
    List<ShopAndOthersDto> otherInsection = new ArrayList<>();
    List<ShopAndOthersDto> shopInpsection = new ArrayList<>();
}
