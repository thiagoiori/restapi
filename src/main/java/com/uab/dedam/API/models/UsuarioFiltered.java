package com.uab.dedam.API.models;

import org.codehaus.jackson.map.annotate.JsonFilter;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonSerialize(include = Inclusion.NON_NULL)
@JsonFilter("anyFilter")
public class UsuarioFiltered extends Usuario {

}
