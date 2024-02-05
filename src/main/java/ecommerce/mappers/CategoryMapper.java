package ecommerce.mappers;

import ecommerce.dtos.CategoryDTO;
import ecommerce.models.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = {ReferenceMapper.class},
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy =  ReportingPolicy.IGNORE
)
public abstract class CategoryMapper {
    public abstract Category map(CategoryDTO categoryDTO);
}
