package io.github.blackfishlabs.domain.model;

import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "FISCAL_NFE")
public class NFeEntity implements Serializable {

    @Id
    private String id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATE")
    private Date createdDate = new DateTime(DateTimeZone.UTC).toDate();

    @Column(name = "EMMITER")
    private String emitter;

    @Column(name = "KEY")
    private String key;

    @Column(name = "PROTOCOL")
    private String protocol;

    @Column(name = "PROTOCOL_CANCEL")
    private String protocolCancel;

    @Column(name = "ENVIRONMENT")
    private String environment;

    @Column(name = "UF")
    private String uf;

    @Lob
    @Column(name = "NFE_XML")
    private String xml;

    @Lob
    @Column(name = "NFE_XML_CANCEL")
    private String xmlCancel;
}