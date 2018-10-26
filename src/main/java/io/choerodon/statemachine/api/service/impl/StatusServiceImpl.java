package io.choerodon.statemachine.api.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.statemachine.api.dto.StatusDTO;
import io.choerodon.statemachine.api.dto.StatusInfoDTO;
import io.choerodon.statemachine.api.dto.StatusMapDTO;
import io.choerodon.statemachine.api.service.StatusService;
import io.choerodon.statemachine.domain.Status;
import io.choerodon.statemachine.fixdata.dto.StatusForMoveDataDO;
import io.choerodon.statemachine.infra.mapper.StateMachineNodeDraftMapper;
import io.choerodon.statemachine.infra.mapper.StateMachineNodeMapper;
import io.choerodon.statemachine.infra.mapper.StatusMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author peng.jiang, dinghuang123@gmail.com
 */
@Service
public class StatusServiceImpl implements StatusService {

    @Autowired
    private StatusMapper stateMapper;

    @Autowired
    private StateMachineNodeDraftMapper nodeMapper;

    @Autowired
    private StateMachineNodeMapper nodeDeployMapper;

    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public Page<StatusDTO> pageQuery(PageRequest pageRequest, StatusDTO statusDTO, String param) {
        Status status = modelMapper.map(statusDTO, Status.class);
        Page<Status> page = PageHelper.doPageAndSort(pageRequest,
                () -> stateMapper.fulltextSearch(status, param));
        List<Status> statuses = page.getContent();
        List<StatusDTO> statusDTOs = modelMapper.map(statuses, new TypeToken<List<StatusDTO>>() {
        }.getType());
        for (StatusDTO dto : statusDTOs) {
            //该状态已被草稿状态机使用个数
            Long draftUsed = nodeMapper.checkStateDelete(dto.getOrganizationId(), dto.getId());
            //该状态已被发布状态机使用个数
            Long deployUsed = nodeDeployMapper.checkStateDelete(dto.getOrganizationId(), dto.getId());
            if (draftUsed == 0 && deployUsed == 0) {
                dto.setCanDelete(true);
            } else {
                dto.setCanDelete(false);
            }
        }
        Page<StatusDTO> returnPage = new Page<>();
        returnPage.setContent(statusDTOs);
        returnPage.setNumber(page.getNumber());
        returnPage.setNumberOfElements(page.getNumberOfElements());
        returnPage.setSize(page.getSize());
        returnPage.setTotalElements(page.getTotalElements());
        returnPage.setTotalPages(page.getTotalPages());
        return returnPage;
    }

    @Override
    public StatusDTO create(Long organizationId, StatusDTO statusDTO) {
        statusDTO.setOrganizationId(organizationId);
        Status status = modelMapper.map(statusDTO, Status.class);
        int isInsert = stateMapper.insert(status);
        if (isInsert != 1) {
            throw new CommonException("error.status.create");
        }
        status = stateMapper.queryById(organizationId, status.getId());
        return modelMapper.map(status, StatusDTO.class);
    }

    @Override
    public StatusDTO update(StatusDTO statusDTO) {
        Status status = modelMapper.map(statusDTO, Status.class);
        int isUpdate = stateMapper.updateByPrimaryKeySelective(status);
        if (isUpdate != 1) {
            throw new CommonException("error.status.update");
        }
        status = stateMapper.queryById(status.getOrganizationId(), status.getId());
        return modelMapper.map(status, StatusDTO.class);
    }

    @Override
    public Boolean delete(Long organizationId, Long statusId) {
        Status status = stateMapper.queryById(organizationId, statusId);
        if (status == null) {
            throw new CommonException("error.status.delete.nofound");
        }
        Long draftUsed = nodeMapper.checkStateDelete(organizationId, statusId);
        Long deployUsed = nodeDeployMapper.checkStateDelete(organizationId, statusId);
        if (draftUsed != 0 || deployUsed != 0) {
            throw new CommonException("error.status.delete");
        }
        int isDelete = stateMapper.deleteByPrimaryKey(statusId);
        if (isDelete != 1) {
            throw new CommonException("error.status.delete");
        }
        return true;
    }

    @Override
    public StatusInfoDTO queryStatusById(Long organizationId, Long stateId) {
        Status status = stateMapper.queryById(organizationId, stateId);
        if (status == null) {
            throw new CommonException("error.queryStatusById.notExist");
        }
        return modelMapper.map(status, StatusInfoDTO.class);
    }

    @Override
    public List<StatusDTO> queryAllStatus(Long organizationId) {
        Status status = new Status();
        status.setOrganizationId(organizationId);
        List<Status> statuses = stateMapper.select(status);
        return modelMapper.map(statuses, new TypeToken<List<StatusDTO>>() {
        }.getType());
    }

    @Override
    public Map<Long, StatusMapDTO> queryAllStatusMap(Long organizationId) {
        Status status = new Status();
        status.setOrganizationId(organizationId);
        List<Status> statuses = stateMapper.select(status);
        Map<Long, StatusMapDTO> statusMap = new HashMap<>();
        for (Status sta : statuses) {
            StatusMapDTO statusMapDTO = modelMapper.map(sta, new TypeToken<StatusMapDTO>() {}.getType());
            statusMap.put(statusMapDTO.getId(), statusMapDTO);
        }
        return statusMap;
    }

    @Override
    public Boolean checkName(Long organizationId, Long statusId, String name) {
        Status status = new Status();
        status.setOrganizationId(organizationId);
        status.setName(name);
        status = stateMapper.selectOne(status);
        if (status != null) {
            //若传了id，则为更新校验（更新校验不校验本身），不传为创建校验
            return status.getId().equals(statusId);
        }
        return true;
    }

    @Override
    public Map<Long, Status> batchStatusGet(List<Long> ids) {
        List<Status> statuses = stateMapper.batchStatusGet(ids);
        Map<Long, Status> map = new HashMap();
        for (Status status : statuses) {
            map.put(status.getId(), status);
        }
        return map;
    }
}
