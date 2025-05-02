update veiculoottarquivo ve
set descricaodocumento = (select doc.descricao from documentoveiculoott doc where doc.id = ve.documentoveiculo_id),
    extensoespermitidas = (select doc.extensoespermitidas from documentoveiculoott doc where doc.id = ve.documentoveiculo_id),
    alugado = (select doc.alugado from documentoveiculoott doc where doc.id = ve.documentoveiculo_id),
    renovacao = (select doc.renovacao from documentoveiculoott doc where doc.id = ve.documentoveiculo_id),
    obrigatorio = (select doc.obrigatorio from documentoveiculoott doc where doc.id = ve.documentoveiculo_id);

update veiculoottarquivo set documentoveiculo_id = null;
