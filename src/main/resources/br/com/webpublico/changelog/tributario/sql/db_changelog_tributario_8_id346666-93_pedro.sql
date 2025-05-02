update operadorarenovacaoottarq op
set descricaodocumento = (select doc.descricao from documentocredenciamentoott doc where doc.id = op.documentocredenciamento_id),
    extensoespermitidas = (select doc.extensoespermitidas from documentocredenciamentoott doc where doc.id = op.documentocredenciamento_id),
    obrigatorio = (select doc.obrigatorio from documentocredenciamentoott doc where doc.id = op.documentocredenciamento_id),
    renovacao = (select doc.renovacao from documentocredenciamentoott doc where doc.id = op.documentocredenciamento_id);

update operadorarenovacaoottarq set documentocredenciamento_id = null;
