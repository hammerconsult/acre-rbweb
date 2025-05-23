update pessoa set arquivo_id  = (select arquivo_id from pessoafisica pf where pf.id = pessoa.id)
