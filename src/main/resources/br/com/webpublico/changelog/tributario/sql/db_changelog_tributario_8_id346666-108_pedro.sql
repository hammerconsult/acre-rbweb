update condutorottarquivo c
set descricaodocumento = (select doc.descricao from documentocondutorott doc where doc.id = c.documentocondutor_id),
    extensoespermitidas = (select doc.extensoespermitidas from documentocondutorott doc where doc.id = c.documentocondutor_id),
    servidorpublico = (select doc.servidorpublico from documentocondutorott doc where doc.id = c.documentocondutor_id),
    renovacao = (select doc.renovacao from documentocondutorott doc where doc.id = c.documentocondutor_id),
    obrigatorio = (select doc.obrigatorio from documentocondutorott doc where doc.id = c.documentocondutor_id);

update condutorottarquivo set documentocondutor_id = null;
