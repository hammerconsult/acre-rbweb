create or replace package pkg_ids_parcelavalordivida
as
    type idsArray is table of number index by varchar2(100);
    newRows idsArray;
    empty idsArray;
end;
