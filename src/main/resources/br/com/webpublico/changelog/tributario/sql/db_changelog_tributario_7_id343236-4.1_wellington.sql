begin
  execute immediate 'update responsavelservico set pessoa_id = pessoafisica_id';
exception
  when others then
  dbms_output.put_line(SQLERRM);
end;
