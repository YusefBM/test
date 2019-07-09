package com.github.dperezcabrera.test.architecture.auth.entities;

import com.github.dperezcabrera.test.architecture.common.AuditableEntity;
import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "permissions")
@NoArgsConstructor
@AllArgsConstructor
public class Permission extends AuditableEntity implements Serializable {

    @EmbeddedId
    private PermissionId id;
}
