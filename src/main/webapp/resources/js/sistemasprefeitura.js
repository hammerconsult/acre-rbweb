function Confirma() {
    if (!confirm('Deseja ativar este usuário?'))
        return false;
}

function comprovarSenha() {
    senha1 = document.formulario.senha1.value
    senha2 = document.formulario.senha2.value

    if (document.formulario.senha1.value == document.formulario.senha2.value)
        alert("As duas senhas são diferentes...\nRealizaríamos as ações do caso negativo");
    return false;
}
