update reconhecimentodivida set exercicio_id = (select ex.id from exercicio ex where ano = extract(year from dataReconhecimento))
