create or replace view VWRHCONTRATO   AS
  SELECT vinc.id AS id_vinculo,
    mat.matricula,
    vinc.numero,
    pf.nome,
    cargo.descricao     AS cargo,
    vinc.iniciovigencia AS iniciovinculo,
    vinc.finalvigencia  AS finalvinculo,
    uo.descricao,
    lot.iniciovigencia AS iniciolotacao,
    lot.finalvigencia  AS finalotacao,
    uo.id              AS id_unidade_lotada,
    pf.id as id_pessoafisica
  FROM pessoafisica pf
  JOIN matriculafp mat
  ON mat.pessoa_id = pf.id
  JOIN vinculofp vinc
  ON vinc.matriculafp_id = mat.id
  JOIN contratofp cont
  ON cont.id = vinc.id
  JOIN cargo cargo
  ON cargo.id = cont.cargo_id
  LEFT JOIN lotacaofuncional lot
  ON lot.vinculofp_id = vinc.id
  LEFT JOIN unidadeorganizacional uo
  ON uo.id = lot.unidadeorganizacional_id