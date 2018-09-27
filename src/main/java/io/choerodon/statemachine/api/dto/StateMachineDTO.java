package io.choerodon.statemachine.api.dto;

import java.util.List;

/**
 * @author peng.jiang@hand-china.com
 */
public class StateMachineDTO {

    private Long id;
    private String name;
    private String description;
    private String status;
    private Long organizationId;
    private Long objectVersionNumber;

    private List<StateMachineNodeDTO> nodeDTOs;
    private List<StateMachineTransfDTO> transfDTOs;

    public StateMachineDTO() {
    }

    public StateMachineDTO(String name, String description, Long organizationId) {
        this.name = name;
        this.description = description;
        this.organizationId = organizationId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public List<StateMachineNodeDTO> getNodeDTOs() {
        return nodeDTOs;
    }

    public void setNodeDTOs(List<StateMachineNodeDTO> nodeDTOs) {
        this.nodeDTOs = nodeDTOs;
    }

    public List<StateMachineTransfDTO> getTransfDTOs() {
        return transfDTOs;
    }

    public void setTransfDTOs(List<StateMachineTransfDTO> transfDTOs) {
        this.transfDTOs = transfDTOs;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
