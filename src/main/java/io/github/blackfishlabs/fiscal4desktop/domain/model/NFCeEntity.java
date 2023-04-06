package io.github.blackfishlabs.fiscal4desktop.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "FISCAL_NFCE")
public class NFCeEntity implements Serializable {

    @Id
    private String id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATE")
    private Date createdDate = new DateTime(DateTimeZone.UTC).toDate();

    @Column(name = "EMITTER")
    private String emitter;

    @Column(name = "STRING_KEY")
    private String key;

    @Column(name = "PROTOCOL")
    private String protocol;

    @Column(name = "PROTOCOL_CANCELED")
    private String protocolCancel;

    @Column(name = "ENVIRONMENT")
    private String environment;

    @Column(name = "UF")
    private String uf;

    @Column(name = "QR_CODE", length = 5000)
    private String qrCode;

    @Lob
    @Column(name = "STRING_XML")
    private String xml;

    @Lob
    @Column(name = "STRING_XML_CANCELED")
    private String xmlCancel;
}
