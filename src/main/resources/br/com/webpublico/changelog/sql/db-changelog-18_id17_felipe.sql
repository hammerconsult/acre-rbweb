create or replace function split(input_list varchar2, ret_this_one number, delimiter varchar2)
return varchar2
is
v_list varchar2(2000) := delimiter || input_list;
start_position number;
end_position number;
begin
start_position := instr(v_list, delimiter, 1, ret_this_one);
if start_position > 0 then
 end_position := instr( v_list, delimiter, 1, ret_this_one + 1);
if end_position = 0 then
 end_position := length(v_list) + 1;
end if;
 return(substr(v_list, start_position + 1, end_position - start_position - 1));
else
 return NULL;
end if;
 end split;