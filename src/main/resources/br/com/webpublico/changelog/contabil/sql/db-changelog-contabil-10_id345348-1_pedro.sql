create or replace trigger ATUALIZANIVELESTRUTURAHIERARQUIA
    before insert or update on HIERARQUIAORGANIZACIONAL
    for each row
begin
    :new.NIVELNAENTIDADE := NIVELESTRUTURA(:new.CODIGO, '.');
    :new.INDICEDONO := NIVELESTRUTURA(:new.CODIGO, '.');
end;
