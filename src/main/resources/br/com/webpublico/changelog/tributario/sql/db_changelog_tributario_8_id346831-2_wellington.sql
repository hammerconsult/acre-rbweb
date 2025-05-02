update quadra set codigo = lpad(trim(codigo), 4, '0') where length(trim(codigo)) < 4
