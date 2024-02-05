package ecommerce.mappers;

import ecommerce.dtos.BrandDTO;
import ecommerce.models.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = {ReferenceMapper.class},
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy =  ReportingPolicy.IGNORE
)
public abstract class BrandMapper {
    public abstract Brand map(BrandDTO brandDTO);
}
