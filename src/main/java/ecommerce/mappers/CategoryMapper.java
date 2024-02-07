package ecommerce.mappers;

import ecommerce.dtos.category.CategoryDTO;
import ecommerce.models.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = {ReferenceMapper.class},
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy =  ReportingPolicy.IGNORE
)
public abstract class CategoryMapper {
    public abstract Category map(CategoryDTO categoryDTO);

    public abstract CategoryDTO map(Category category);

    public abstract void update(CategoryDTO dto, @MappingTarget Category model);
}
