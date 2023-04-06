package io.github.blackfishlabs.fiscal4desktop.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

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
    @Column(name = "STRING_XML")
    private String xml;

    @Column(name = "EMITTER")
    private String emitter;

    @Column(name = "STRING_KEY")
    private String key;
}
