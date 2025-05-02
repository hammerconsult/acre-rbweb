create or replace package pkg_fks as
    type fk_record is record (
                                 table_name varchar2(100),
                                 column_name varchar2(100)
                             );

    type fk_table is table of fk_record;

    function get_fks(p_table_name varchar2)
        return fk_table pipelined;

    function has_record(p_table varchar2, p_column varchar2, p_value numeric)
        return number;

    function get_fks_with_movement(p_table_name varchar2, p_id numeric)
        return fk_table pipelined;

end;
