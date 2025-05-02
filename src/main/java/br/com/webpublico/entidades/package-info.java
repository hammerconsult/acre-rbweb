@TypeDefs({@TypeDef(name = "direitos", typeClass = DireitosUserType.class),
        @TypeDef(name = "tipoGrupoUsuario", typeClass = TipoGrupoUsuarioUserType.class)}) package br.com.webpublico.entidades;

import br.com.webpublico.entidades.usertype.DireitosUserType;
import br.com.webpublico.entidades.usertype.TipoGrupoUsuarioUserType;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

