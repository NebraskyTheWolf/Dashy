package eu.fluffici.data.network.model;

import kotlinx.serialization.Serializable;

@Serializable
public class AuditS {
    public Integer id;
    public String name;
    public String type;
    public String slug;
    public String created_at;
}
