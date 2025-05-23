insert into construcaocivil (id)
select dec.construcaocivil_id from declaracaoprestacaoservico dec
         left join construcaocivil cc on cc.id = dec.construcaocivil_id
where dec.construcaocivil_id is not null
  and cc.id is null;

alter table declaracaoprestacaoservico add constraint fk_dec_construcao
    foreign key (construcaocivil_id) references construcaocivil (id);
