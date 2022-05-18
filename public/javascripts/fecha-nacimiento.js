const MAX_DIAS=31
const MAX_MESES=12
const YEAR_ACTUAL=2017
const YEAR_MINIMO=1900
var meses=['Enero','Febrero','Marzo','Abril','Mayo','Junio','Julio','Agosto','Septiembre','Octubre','Noviembre','Diciembre'];

function dropdownfechanacimiento(dia, mes, anyo, fecha) {
    var dia=document.getElementById(dia)
    var mes=document.getElementById(mes)
    var anyo=document.getElementById(anyo)
    if(fecha !== ''){
        var diaEntero = parseInt(fecha.split('-')[2])
        var mesEntero = parseInt(fecha.split('-')[1])
        var anyoEntero = parseInt(fecha.split('-')[0])
    }

    for (var i=0; i<=MAX_DIAS; i++) {
        if(i==0) {
            if(fecha === '') dia.options[i]=new Option('Día', '', true, true)
            else dia.options[i]=new Option('Día', '')
        }
        else if(fecha !== '' && i==diaEntero) dia.options[i]=new Option(diaEntero, diaEntero, true, true)
        else dia.options[i]=new Option(i, i)
    }

    for (var m=0; m<=MAX_MESES; m++) {
        if(m==0) {
            if(fecha === '') mes.options[m]=new Option('Mes', '', true, true)
            else mes.options[m]=new Option('Mes', '')
        }
        else if(fecha !== '' && m==mesEntero) mes.options[m]=new Option(meses[m-1], mesEntero, true, true)
        else mes.options[m]=new Option(meses[m-1], m)
    }

    var anyo_actual=YEAR_ACTUAL
    for (var y=0; y<=(YEAR_ACTUAL-YEAR_MINIMO+1); y++){
        if(y==0) {
            if(fecha === '') anyo.options[y]=new Option('Año', '', true, true)
            else anyo.options[y]=new Option('Año', '')
            continue
        }
        else if(fecha !== '' && anyo_actual==anyoEntero) anyo.options[y]=new Option(anyoEntero, anyoEntero, true, true)
        else anyo.options[y]=new Option(anyo_actual, anyo_actual)
        anyo_actual-=1
    }
}
