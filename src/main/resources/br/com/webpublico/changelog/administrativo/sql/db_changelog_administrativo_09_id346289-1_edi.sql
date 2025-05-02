update ajustecontrato aj
set aj.responsavel_id = (select usu.pessoafisica_id
                         from ajustecontrato_aud aud
                                  inner join revisaoauditoria rev on rev.id = aud.rev
                                  inner join usuariosistema usu on usu.login = rev.usuario
                         where aud.revtype = 0
                           and aud.id = aj.id
                         order by aud.id fetch first 1 row only)
where responsavel_id is null;

update ajustecontrato set tipoajuste = 'CONTRATO'
where tipoajuste is null;
