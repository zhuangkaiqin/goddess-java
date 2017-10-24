package com.bjike.goddess.financeinit.service;

import com.bjike.goddess.common.api.dto.Restrict;
import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.common.jpa.service.ServiceImpl;
import com.bjike.goddess.common.provider.utils.RpcTransmit;
import com.bjike.goddess.common.utils.bean.BeanTransform;
import com.bjike.goddess.financeinit.bo.CategoryBO;
import com.bjike.goddess.financeinit.bo.FirstSubjectBO;
import com.bjike.goddess.financeinit.dto.CategoryDTO;
import com.bjike.goddess.financeinit.dto.FirstSubjectDTO;
import com.bjike.goddess.financeinit.entity.Category;
import com.bjike.goddess.financeinit.entity.FirstSubject;
import com.bjike.goddess.financeinit.enums.CategoryName;
import com.bjike.goddess.financeinit.enums.GuideAddrStatus;
import com.bjike.goddess.financeinit.to.CategoryTO;
import com.bjike.goddess.financeinit.to.GuidePermissionTO;
import com.bjike.goddess.user.api.UserAPI;
import com.bjike.goddess.user.bo.UserBO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 类别业务实现
 *
 * @Author: [ tanghaixiang ]
 * @Date: [ 2017-03-29 04:18 ]
 * @Description: [ 类别业务实现 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@CacheConfig(cacheNames = "financeinitSerCache")
@Service
public class CategorySerImpl extends ServiceImpl<Category, CategoryDTO> implements CategorySer {

    @Autowired
    private FirstSubjectSer firstSubjectSer;
    @Autowired
    private UserAPI userAPI;
    @Autowired
    private CusPermissionSer cusPermissionSer;

    /**
     * 核对查看权限（部门级别）
     */
    private void checkSeeIdentity() throws SerException {
        Boolean flag = false;
        String userToken = RpcTransmit.getUserToken();
        UserBO userBO = userAPI.currentUser();
        RpcTransmit.transmitUserToken(userToken);
        String userName = userBO.getUsername();
        if (!"admin".equals(userName.toLowerCase())) {
            flag = cusPermissionSer.getCusPermission("1");
            if (!flag) {
                throw new SerException("您不是相应财务部门的人员，不可以查看");
            }
        }
        RpcTransmit.transmitUserToken(userToken);

    }

    /**
     * 核对添加修改删除审核权限（岗位级别）
     */
    private void checkAddIdentity() throws SerException {
        Boolean flag = false;
        String userToken = RpcTransmit.getUserToken();
        UserBO userBO = userAPI.currentUser();
        RpcTransmit.transmitUserToken(userToken);
        String userName = userBO.getUsername();
        if (!"admin".equals(userName.toLowerCase())) {
            flag = cusPermissionSer.busCusPermission("2");
            if (!flag) {
                throw new SerException("您不是相应财务部门的人员，不可以操作");
            }
        }
        RpcTransmit.transmitUserToken(userToken);

    }

    /**
     * 导航栏核对查看权限（部门级别）
     */
    private Boolean guideSeeIdentity() throws SerException {
        Boolean flag = false;
        String userToken = RpcTransmit.getUserToken();
        UserBO userBO = userAPI.currentUser();
        RpcTransmit.transmitUserToken(userToken);
        String userName = userBO.getUsername();
        if (!"admin".equals(userName.toLowerCase())) {
            flag = cusPermissionSer.getCusPermission("1");
        } else {
            flag = true;
        }
        return flag;
    }

    /**
     * 导航栏核对添加修改删除审核权限（岗位级别）
     */
    private Boolean guideAddIdentity() throws SerException {
        Boolean flag = false;
        String userToken = RpcTransmit.getUserToken();
        UserBO userBO = userAPI.currentUser();
        RpcTransmit.transmitUserToken(userToken);
        String userName = userBO.getUsername();
        if (!"admin".equals(userName.toLowerCase())) {
            flag = cusPermissionSer.busCusPermission("2");
        } else {
            flag = true;
        }
        return flag;
    }

    @Override
    public Boolean sonPermission() throws SerException {
        String userToken = RpcTransmit.getUserToken();
        Boolean flagSee = guideSeeIdentity();
        RpcTransmit.transmitUserToken(userToken);
        Boolean flagAdd = guideAddIdentity();
        if (flagSee || flagAdd) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public Boolean guidePermission(GuidePermissionTO guidePermissionTO) throws SerException {
        String userToken = RpcTransmit.getUserToken();
        GuideAddrStatus guideAddrStatus = guidePermissionTO.getGuideAddrStatus();
        Boolean flag = true;
        switch (guideAddrStatus) {
            case LIST:
                flag = guideSeeIdentity();
                break;
            case ADD:
                flag = guideAddIdentity();
                break;
            case EDIT:
                flag = guideAddIdentity();
                break;
            case DELETE:
                flag = guideAddIdentity();
                break;
            default:
                flag = true;
                break;
        }

        RpcTransmit.transmitUserToken(userToken);
        return flag;
    }

    @Override
    public List<String> listFirstName(CategoryTO categoryTO) throws SerException {
        FirstSubjectDTO firstSubjectDTO = new FirstSubjectDTO();
        switch (categoryTO.getCategoryName().name()) {
            case "ASSETS":
                firstSubjectDTO.getConditions().add(Restrict.eq("category", "资产类"));
                break;
            case "LIABILITIES":
                firstSubjectDTO.getConditions().add(Restrict.eq("category", "负债类"));
                break;
            case "COMMON":
                firstSubjectDTO.getConditions().add(Restrict.eq("category", "共同类"));
                break;
            case "RIGHTSINTERESTS":
                firstSubjectDTO.getConditions().add(Restrict.eq("category", "权益类"));
                break;
            case "COST":
                firstSubjectDTO.getConditions().add(Restrict.eq("category", "成本类"));
                break;
            case "PROFITLOSS":
                firstSubjectDTO.getConditions().add(Restrict.eq("category", "损益类"));
                break;
            default:
                firstSubjectDTO.getConditions().add(Restrict.eq("category", "资产类"));
                break;
        }
        List<FirstSubject> list = firstSubjectSer.findByCis(firstSubjectDTO);
        List<String> firstName = list.stream().map(FirstSubject::getName).collect(Collectors.toList());
        return firstName;
    }

    @Override
    public Long countCategory(CategoryDTO categoryDTO) throws SerException {
        switch (categoryDTO.getCategoryName().name()) {
            case "ASSETS":
                categoryDTO.getConditions().add(Restrict.eq("categoryName", 0));
                break;
            case "LIABILITIES":
                categoryDTO.getConditions().add(Restrict.eq("categoryName", 1));
                break;
            case "COMMON":
                categoryDTO.getConditions().add(Restrict.eq("categoryName", 2));
                break;
            case "RIGHTSINTERESTS":
                categoryDTO.getConditions().add(Restrict.eq("categoryName", 3));
                break;
            case "COST":
                categoryDTO.getConditions().add(Restrict.eq("categoryName", 4));
                break;
            case "PROFITLOSS":
                categoryDTO.getConditions().add(Restrict.eq("categoryName", 5));
                break;
            default:
                categoryDTO.getConditions().add(Restrict.eq("categoryName", 0));
                break;
        }
        Long count = super.count(categoryDTO);
        return count;
    }

    @Override
    public CategoryBO getOneById(String id) throws SerException {
        if (StringUtils.isBlank(id)) {
            throw new SerException("id不能呢为空");
        }

        Category category = super.findById(id);
        FirstSubjectBO firstSubjectBO = BeanTransform.copyProperties(category.getFirstSubject(), FirstSubjectBO.class);
        CategoryBO categoryBO = BeanTransform.copyProperties(category, CategoryBO.class);
        categoryBO.setFirstSubjectBO(firstSubjectBO);
        return categoryBO;
    }

    @Override
    public List<CategoryBO> listCategory(CategoryDTO categoryDTO) throws SerException {
        checkSeeIdentity();

        switch (categoryDTO.getCategoryName().name()) {
            case "ASSETS":
                categoryDTO.getConditions().add(Restrict.eq("categoryName", 0));
                break;
            case "LIABILITIES":
                categoryDTO.getConditions().add(Restrict.eq("categoryName", 1));
                break;
            case "COMMON":
                categoryDTO.getConditions().add(Restrict.eq("categoryName", 2));
                break;
            case "RIGHTSINTERESTS":
                categoryDTO.getConditions().add(Restrict.eq("categoryName", 3));
                break;
            case "COST":
                categoryDTO.getConditions().add(Restrict.eq("categoryName", 4));
                break;
            case "PROFITLOSS":
                categoryDTO.getConditions().add(Restrict.eq("categoryName", 5));
                break;
            default:
                categoryDTO.getConditions().add(Restrict.eq("categoryName", 0));
                break;
        }
        List<Category> list = super.findByCis(categoryDTO, true);
        List<CategoryBO> categoryBOList = new ArrayList<>();
        list.stream().forEach(str -> {
            FirstSubjectBO firstSubjectBO = BeanTransform.copyProperties(str.getFirstSubject(), FirstSubjectBO.class);
            CategoryBO cb = BeanTransform.copyProperties(str, CategoryBO.class);
            cb.setFirstSubjectBO(firstSubjectBO);
            categoryBOList.add(cb);
        });
        return categoryBOList;
    }

    @Transactional(rollbackFor = SerException.class)
    @Override
    public CategoryBO addCategory(CategoryTO categoryTO) throws SerException {
        checkAddIdentity();

        String firstSubjectName = categoryTO.getFirstSubjectName();
        FirstSubjectBO firstSubjectBO = firstSubjectSer.getFirstSubject(firstSubjectName);
        if (firstSubjectBO == null || StringUtils.isBlank(firstSubjectBO.getCode())) {
            throw new SerException("不存在该一级科目，请重新选择一级科目");
        }
        CategoryDTO dto = new CategoryDTO();
        dto.getConditions().add(Restrict.eq("secondSubject", categoryTO.getSecondSubject()));
        dto.getConditions().add(Restrict.eq("thirdSubject", categoryTO.getThirdSubject()));
        FirstSubject firstSubjectTemp = BeanTransform.copyProperties(firstSubjectBO, FirstSubject.class, true);
        dto.getConditions().add(Restrict.eq("firstSubject.id", firstSubjectTemp.getId()));
        Long count = super.count(dto);
        if (count > 0) {
            throw new SerException("已存在相同数据");
        }

        String categoryFromFirstSubject = firstSubjectBO.getCategory();
        CategoryName categoryName = CategoryName.ASSETS;
        switch (categoryFromFirstSubject) {
            case "资产类":
                categoryName = CategoryName.ASSETS;
                break;
            case "负债类":
                categoryName = CategoryName.LIABILITIES;
                break;
            case "共同类":
                categoryName = CategoryName.COMMON;
                break;
            case "权益类":
                categoryName = CategoryName.RIGHTSINTERESTS;
                break;
            case "成本类":
                categoryName = CategoryName.COST;
                break;
            case "损益类":
                categoryName = CategoryName.PROFITLOSS;
                break;
            default:
                categoryName = CategoryName.ASSETS;
                break;
        }

        //1000 2000 3000 ...
        dto = new CategoryDTO();
        String code = generateCode(categoryTO, firstSubjectBO, dto);

        FirstSubject firstSubject = BeanTransform.copyProperties(firstSubjectBO, FirstSubject.class, true);
        Category category = BeanTransform.copyProperties(categoryTO, Category.class, true);
        category.setFirstSubject(firstSubject);
        category.setCreateTime(LocalDateTime.now());
        category.setCategoryName(categoryName);
        category.setCode(code);
        super.save(category);
        return BeanTransform.copyProperties(category, CategoryBO.class);
    }


    @Transactional(rollbackFor = SerException.class)
    @Override
    public CategoryBO editCategory(CategoryTO categoryTO) throws SerException {
        checkAddIdentity();

        String firstSubjectName = categoryTO.getFirstSubjectName();
        String secondSubjectName = categoryTO.getSecondSubject();
        String thirdSubjectName = categoryTO.getThirdSubject();
        FirstSubjectBO firstSubjectBO = firstSubjectSer.getFirstSubject(firstSubjectName);
        if (firstSubjectBO == null || StringUtils.isBlank(firstSubjectBO.getCode())) {
            throw new SerException("不存在该一级科目，请重新选择一级科目");
        }

        Category tranCategory = BeanTransform.copyProperties(categoryTO, Category.class, true);
        Category category = super.findById(categoryTO.getId());

        if (category.getFirstSubject().getName().equals(categoryTO.getFirstSubjectName())
                && category.getSecondSubject().equals(categoryTO.getSecondSubject())
                && category.getThirdSubject().equals(categoryTO.getThirdSubject())) {
            category.setRemark(tranCategory.getRemark());
            super.update(category);
            return BeanTransform.copyProperties(category, CategoryBO.class);
        } else {
            CategoryDTO dto = new CategoryDTO();
            if (!firstSubjectBO.getId().equals(category.getFirstSubject().getId())) {
                dto.getConditions().add(Restrict.eq("firstSubject.id", firstSubjectBO.getId()));
                dto.getConditions().add(Restrict.eq("secondSubject", secondSubjectName));
                if (null == thirdSubjectName) {
                    dto.getConditions().add(Restrict.isNull("thirdSubject"));
                } else {
                    dto.getConditions().add(Restrict.eq("thirdSubject", thirdSubjectName));
                }
                List<Category> otherList = super.findByCis(dto);
                if (otherList != null && otherList.size() > 0) {
                    throw new SerException("该一级科目,二级科目，三级科目已经存在，不可以修改，请重新选择一级科目，二级科目，三级科目");
                }
            }

            dto = new CategoryDTO();
            String code = generateCode(categoryTO, firstSubjectBO, dto);


//            BeanUtils.copyProperties(tranCategory, category, "id", "firstSubject_id", "createTime", "firstCode", "secondCode");
            category.setFirstSubject(BeanTransform.copyProperties(firstSubjectBO, FirstSubject.class, true));
            category.setCode(code);
            category.setFirstCode(category.getFirstSubject().getCode());
            category.setSecondCode(categoryTO.getSecondCode());
            category.setThirdCode(categoryTO.getThirdCode());
            category.setSecondSubject(tranCategory.getSecondSubject());
            category.setThirdSubject(tranCategory.getThirdSubject());
            category.setRemark(tranCategory.getRemark());

            category.setModifyTime(LocalDateTime.now());
            super.update(category);
            return BeanTransform.copyProperties(category, CategoryBO.class);
        }
    }

    @Transactional(rollbackFor = SerException.class)
    @Override
    public void deleteCategory(String id) throws SerException {
        super.remove(id);
    }

    @Override
    public List<String> getSecondSubject(CategoryDTO categoryDTO) throws SerException {
        String[] fields = new String[]{"secondSubject"};
        List<CategoryBO> categoryBOList = super.findBySql("select fc.secondSubject  from financeinit_category fc " +
                " inner join financeinit_firstsubject ff on fc.firstSubject_id = ff.id where ff.name = '" + categoryDTO.getFirstSubjectName() + "'", CategoryBO.class, fields);

        List<String> list = categoryBOList.stream().map(CategoryBO::getSecondSubject)
                .filter(name -> (name != null || !"".equals(name.trim()))).distinct().collect(Collectors.toList());

        return list;
    }

    @Override
    public List<String> getThirdSubject(CategoryDTO categoryDTO) throws SerException {
        String[] fields = new String[]{"thirdSubject"};
        List<CategoryBO> categoryBOList = super.findBySql("select fc.thirdSubject  from financeinit_category fc " +
                " inner join financeinit_firstsubject ff on fc.firstSubject_id = ff.id " +
                " where ff.name = '" + categoryDTO.getFirstSubjectName() + "' and fc.secondSubject = '" + categoryDTO.getSecondSubject() + "'", CategoryBO.class, fields);

        List<String> list = categoryBOList.stream().map(CategoryBO::getThirdSubject)
                .filter(name -> (name != null || !"".equals(name.trim()))).distinct().collect(Collectors.toList());

        return list;
    }


    public String generateCode(CategoryTO categoryTO, FirstSubjectBO firstSubjectBO, CategoryDTO dto) throws SerException {
        String code = "";
//        CategoryDTO cdto = new CategoryDTO();
        String firstSubjectCode = firstSubjectBO.getCode();
        categoryTO.setFirstCode(firstSubjectCode);

        switch (firstSubjectBO.getCategory()) {
            case "资产类":
                dto.getConditions().add(Restrict.eq("categoryName", 0));
                break;
            case "负债类":
                dto.getConditions().add(Restrict.eq("categoryName", 1));
                break;
            case "共同类":
                dto.getConditions().add(Restrict.eq("categoryName", 2));
                break;
            case "权益类":
                dto.getConditions().add(Restrict.eq("categoryName", 3));
                break;
            case "成本类":
                dto.getConditions().add(Restrict.eq("categoryName", 4));
                break;
            case "损益类":
                dto.getConditions().add(Restrict.eq("categoryName", 5));
                break;
            default:
                dto.getConditions().add(Restrict.eq("categoryName", 0));
                break;
        }


        if (StringUtils.isNotBlank(categoryTO.getFirstSubjectName())) {
            dto.getConditions().add(Restrict.eq("firstSubject.id", firstSubjectBO.getId()));
        }
        //有一二级情况下
        if (StringUtils.isNotBlank(categoryTO.getSecondSubject())
                && StringUtils.isBlank(categoryTO.getThirdSubject())) {
            dto.getConditions().add(Restrict.eq("secondSubject", categoryTO.getSecondSubject()));
            List<Category> categories = super.findByCis(dto);
            if (categories != null && categories.size() > 0) {
                throw new SerException("已存在该一级科目下的二级科目");

            } else {
                //不存在就要取最大加一
                CategoryDTO temp_cdto = new CategoryDTO();
                temp_cdto.getConditions().add(Restrict.eq("firstSubject.id", firstSubjectBO.getId()));
                temp_cdto.getSorts().add("secondCode=desc");
                List<Category> tempCategory = super.findByCis(temp_cdto);
                if (tempCategory != null && tempCategory.size() > 0) {
                    Category maxCodeCategory = tempCategory.get(0);
                    Long secondCode = Long.parseLong(maxCodeCategory.getSecondCode()) + 1;
                    if (secondCode < 99) {
                        code = firstSubjectCode + String.valueOf(secondCode < 10 ? "0" + secondCode : secondCode);
                        categoryTO.setSecondCode(String.valueOf(secondCode));
                    } else {
                        throw new SerException("不能再继续添加，二级编号超过了99");
                    }
                } else {
                    code = firstSubjectCode + "01";
                    categoryTO.setSecondCode("01");
                }

            }
            categoryTO.setCode(code);
        } else if (StringUtils.isNotBlank(categoryTO.getSecondSubject())
                && StringUtils.isNotBlank(categoryTO.getThirdSubject())) {
            //先查三级是否有（有：抛异常 无：根据一二级查最大三级加一）
            dto = new CategoryDTO();
            dto.getConditions().add(Restrict.eq("firstSubject.id", firstSubjectBO.getId()));
            dto.getConditions().add(Restrict.eq("secondSubject", categoryTO.getSecondSubject()));
            dto.getConditions().add(Restrict.eq("thirdSubject", categoryTO.getThirdSubject()));
            List<Category> categories = super.findByCis(dto);
            if (categories != null && categories.size() > 0) {
                throw new SerException("已存在该一级科目和二级科目下的三级科目");

            } else {
                //不存在就要取最大加一
                dto = new CategoryDTO();
                dto.getConditions().add(Restrict.eq("firstSubject.id", firstSubjectBO.getId()));
                dto.getConditions().add(Restrict.eq("secondSubject", categoryTO.getSecondSubject()));
                dto.getSorts().add("secondSubject=desc");
                dto.getSorts().add("thirdCode=desc");
                List<Category> tempCategory = super.findByCis(dto);

                //1,2,3都添加了，
                //1,有，2没有，（不为空）

                if (tempCategory != null && tempCategory.size() > 0) {
                    Category maxCodeCategory = tempCategory.get(0);
                    Long thirdCode = Long.parseLong(maxCodeCategory.getThirdCode()) + 1;
                    if (thirdCode < 99) {
                        code = maxCodeCategory.getFirstCode() + maxCodeCategory.getSecondCode() + String.valueOf(thirdCode < 10 ? "0" + thirdCode : thirdCode);
                        categoryTO.setThirdCode(String.valueOf(thirdCode));
                        categoryTO.setSecondCode(String.valueOf(maxCodeCategory.getSecondCode()));
                    } else {
                        throw new SerException("不能再继续添加，二级编号超过了99");
                    }
                } else {
                    //如果二级也不存在
                    //查询二级最大加一
                    dto = new CategoryDTO();
                    dto.getConditions().add(Restrict.eq("firstSubject.id", firstSubjectBO.getId()));
                    dto.getSorts().add("secondCode=desc");
                    categories = super.findByCis(dto);
                    if (categories != null && categories.size() > 0) {
                        Long secondCode = Long.parseLong(categories.get(0).getSecondCode() == null ? "00" : categories.get(0).getSecondCode()) + 1;
                        categoryTO.setSecondCode((secondCode < 10 ? "0" + secondCode : secondCode + ""));
                        categoryTO.setThirdCode("01");
                        code = firstSubjectCode + categoryTO.getSecondCode() + "01";
                        categoryTO.setCode(code);
                    } else {
                        //不存在就要01
                        categoryTO.setSecondCode("01");
                        categoryTO.setThirdCode("01");
                        code = firstSubjectCode + "01" + "01";
                        categoryTO.setCode(code);
                    }

                }

            }
        }

        return code;
    }

    @Override
    public List<CategoryBO> listAllCategory(CategoryDTO categoryDTO) throws SerException {
        if (StringUtils.isNotBlank(categoryDTO.getThirdSubject())) {
            categoryDTO.getConditions().add(Restrict.eq("thirdSubject", categoryDTO.getThirdSubject()));
        }
        if (StringUtils.isNotBlank(categoryDTO.getRemark())) {
            categoryDTO.getConditions().add(Restrict.eq("remark", categoryDTO.getRemark()));
        }
        List<Category> list = super.findByCis(categoryDTO);
        List<CategoryBO> categoryBOList = new ArrayList<>();
        list.stream().forEach(str -> {
            FirstSubjectBO firstSubjectBO = BeanTransform.copyProperties(str.getFirstSubject(), FirstSubjectBO.class);
            CategoryBO cb = BeanTransform.copyProperties(str, CategoryBO.class);
            cb.setFirstSubjectBO(firstSubjectBO);
            categoryBOList.add(cb);
        });
        return categoryBOList;
    }

    @Override
    public CategoryBO listById(String id) throws SerException {
        Category category = super.findById(id);
        FirstSubjectBO firstSubjectBO = BeanTransform.copyProperties(category.getFirstSubject(), FirstSubjectBO.class);
        CategoryBO cb = BeanTransform.copyProperties(category, CategoryBO.class);
        cb.setFirstSubjectBO(firstSubjectBO);
        return cb;
    }

    @Override
    public List<String> listAllThirdName() throws SerException {
        List<String> list = new ArrayList<>();
        CategoryDTO dto = new CategoryDTO();
        String[] field = new String[]{"thirdSubject"};
        String sql = "select thirdSubject from financeinit_category ";
        List<Category> categoryList = super.findBySql(sql, Category.class, field);
        if (categoryList != null && categoryList.size() > 0) {
            list = categoryList.stream().map(Category::getThirdSubject).collect(Collectors.toList());
        }
        return list;
    }

    @Override
    public List<String> findByFirstName(String firstName) throws SerException {
        List<String> list = new ArrayList<>();
        String[] fields = new String[]{"thirdSubject"};
        StringBuilder sql = new StringBuilder(" SELECT thirdSubject FROM financeinit_category a, ");
        sql.append(" (select id from financeinit_firstsubject where name = '" + firstName + "') b ");
        sql.append(" WHERE a.firstSubject_id = b.id; ");
        List<Category> categoryList = super.findBySql(sql.toString(), Category.class, fields);
        if (categoryList != null && categoryList.size() > 0) {
            list = categoryList.stream().map(Category::getThirdSubject).collect(Collectors.toList());
        }
        return list;
    }

    @Override
    public Boolean isAssets(String firstSubject) throws SerException {
        Boolean tar = false;
        FirstSubjectDTO firstSubjectDTO = new FirstSubjectDTO();
        firstSubjectDTO.getConditions().add(Restrict.eq("name", firstSubject));
        FirstSubject firstSubject1 = firstSubjectSer.findOne(firstSubjectDTO);
        if (null != firstSubject1) {
            if ("资产类".equals(firstSubject1.getCategory())) {
                tar = true;
            }
//            else if ("负债类".equals(firstSubject1.getCategory())) {
//                return false;
//            }
        }
        return tar;
    }
}