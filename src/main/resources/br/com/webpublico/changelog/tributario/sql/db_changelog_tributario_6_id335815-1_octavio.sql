delete from cnaealvara ca
where ca.rowid in
      (select rowid from
          (select rowid, row_number() over (
              partition by cnae_id || alvara_id
              order by cnae_id || alvara_id
              ) numlines
          from cnaealvara) t2
      where numlines > 1);
