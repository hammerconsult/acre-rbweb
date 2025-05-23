DECLARE
cursor c1 is
select CARGO.id as id_cargo
from CARGO;
BEGIN
FOR linha in c1
        LOOP
            insert into UNIDADECARGO (ID, CARGO_ID, UNIDADEORGANIZACIONAL_ID)
select HIBERNATE_SEQUENCE.nextval, linha.id_cargo, vw.SUBORDINADA_ID uni_id
from VWHIERARQUIAADMINISTRATIVA vw
         inner join ENTIDADE ent on vw.ENTIDADE_ID = ent.ID
         inner join hierarquiaorganizacional ho on ho.id = vw.ID
where current_date between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, current_date)
  and current_date between ho.INICIOVIGENCIA and coalesce(ho.FIMVIGENCIA, current_date)
  and ho.NIVELNAENTIDADE = 2;
END LOOP;
COMMIT;
END;
