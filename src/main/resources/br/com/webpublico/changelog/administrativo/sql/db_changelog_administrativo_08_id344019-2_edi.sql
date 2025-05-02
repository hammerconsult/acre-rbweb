update ENTRADAMATERIAL ent
set ent.responsavel_id = (select usu.PESSOAFISICA_ID
                          from usuariosistema usu
                                   inner join entradamaterial em on em.USUARIO_ID = usu.id
                          where em.id = ent.id);
