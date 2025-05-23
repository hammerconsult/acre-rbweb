declare
    max_seq INTEGER;
begin
   select max(numerolancamento) +1
   into   max_seq
   from calculorbtrans;

    execute immediate 'Create sequence RBTRANS_SEQUENCE start with ' || max_seq;
end;
