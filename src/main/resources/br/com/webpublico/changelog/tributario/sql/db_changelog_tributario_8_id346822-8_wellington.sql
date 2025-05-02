declare
v_id numeric;
begin
select HIBERNATE_SEQUENCE.nextval into v_id from dual;

insert into configuracaotributariobi (id, debitosgeralexercicioinicialparte1,
                                      debitosgeralexerciciofinalparte1,
                                      debitosgeralexercicioinicialparte2,
                                      debitosgeralexerciciofinalparte2)
values (v_id, 1990, 2000, 2001, 2025);

update configuracaotributario set configuracaotributariobi_id = v_id
where configuracaotributariobi_id is null;
end;
