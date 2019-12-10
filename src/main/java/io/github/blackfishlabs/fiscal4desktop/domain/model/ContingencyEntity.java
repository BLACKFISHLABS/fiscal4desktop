package io.github.blackfishlabs.fiscal4desktop.domain.model;

import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "FISCAL_CONTINGENCY")
public class ContingencyEntity implements Serializable {

    @Id
    private String id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATE")
    private Date createdDate = new DateTime(DateTimeZone.UTC).toDate();

    @Lob
    @Column(name = "NFCE_XML")
    private String xml;

    @Column(name = "EMMITER")
    private String emitter;

    @Column(name = "KEY")
    private String key;
}
