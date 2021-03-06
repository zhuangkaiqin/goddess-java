package com.bjike.goddess.taskallotment.api;

import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.taskallotment.bo.ProjectBO;
import com.bjike.goddess.taskallotment.bo.TableBO;
import com.bjike.goddess.taskallotment.dto.ProjectDTO;
import com.bjike.goddess.taskallotment.entity.Project;
import com.bjike.goddess.taskallotment.service.ProjectSer;
import com.bjike.goddess.taskallotment.to.GuidePermissionTO;
import com.bjike.goddess.taskallotment.to.ProjectTO;
import com.bjike.goddess.taskallotment.to.TableTO;
import com.bjike.goddess.taskallotment.vo.SonPermissionObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 项目列表业务接口实现
 *
 * @Author: [ chenjunhao ]
 * @Date: [ 2017-09-14 11:55 ]
 * @Description: [ 项目列表业务接口实现 ]
 * @Version: [ v1.0.0 ]
 * @Copy: [ com.bjike ]
 */
@Service("projectApiImpl")
public class ProjectApiImpl implements ProjectAPI {
    @Autowired
    private ProjectSer projectSer;

    @Override
    public List<ProjectBO> list(ProjectDTO dto) throws SerException {
        return projectSer.list(dto);
    }

    @Override
    public void save(ProjectTO to) throws SerException {
        projectSer.save(to);
    }

    @Override
    public void edit(ProjectTO to) throws SerException {
        projectSer.edit(to);
    }

    @Override
    public void delete(String id) throws SerException {
        projectSer.delete(id);
    }

    @Override
    public ProjectBO findByID(String id) throws SerException {
        return projectSer.findByID(id);
    }

    @Override
    public Long count(ProjectDTO dto) throws SerException {
        return projectSer.count(dto);
    }

    @Override
    public void editTable(TableTO tableTO) throws SerException {
        projectSer.editTable(tableTO);
    }

    @Override
    public Set<String> areas() throws SerException {
        return projectSer.areas();
    }

    @Override
    public Set<String> departs(ProjectDTO dto) throws SerException {
        return projectSer.departs(dto);
    }

    @Override
    public List<ProjectBO> projects(ProjectDTO dto) throws SerException {
        return projectSer.projects(dto);
    }

    @Override
    public TableBO table(String id) throws SerException {
        return projectSer.table(id);
    }

    @Override
    public List<ProjectBO> list1(ProjectDTO dto) throws SerException {
        return projectSer.list1(dto);
    }

    @Override
    public List<SonPermissionObject> sonPermission() throws SerException {
        return projectSer.sonPermission();
    }

    @Override
    public Boolean guidePermission(GuidePermissionTO guidePermissionTO) throws SerException {
        return projectSer.guidePermission(guidePermissionTO);
    }

    @Override
    public List<ProjectBO> projects() throws SerException {
        return projectSer.projects();
    }

    @Override
    public List<TableBO> tables(String projectId) throws SerException {
        return projectSer.tables(projectId);
    }

    @Override
    public Set<String> taskNames(String tableId) throws SerException {
        return projectSer.taskNames(tableId);
    }
}