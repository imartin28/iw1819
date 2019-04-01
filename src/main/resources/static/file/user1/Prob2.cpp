// Grupo E, Sergio Martin Gomez y Adrian Sanchez
//La especificación indica que b debe ser cierto si, dada una posicion p del vector, el elemento p y todos los elementos previos 
//a ese elemento son menores que todos los posteriores

/*
Para resolverlo se declaran dos variables que son el maximo a la izquierda de p y el minimo a la derecha de p, entonces se recorre el array desde 
la posicion 0 hasta la posicion p buscando el valor maximo de ese subsegmento del vector, se hace lo mismo con el subsegmento a la derecha 
de p buscando el minimo. Por útlimo se comparan los valores para saber si b es TRUE o FALSE
*/
#include <iostream>
#include <iomanip>
#include <fstream>
#include <vector>
#include <assert.h>
#include <stdio.h>
#include <algorithm>

using namespace std;


// función que resuelve el problema
// comentario sobre el coste, O(f(N))
bool resolver(vector<int> vector, int longVector, int p) {

	if(p == longVector - 1)	//p corresponde al último elemento del vector, eso significa que es el conjunto vacío, por lo tanto el cuantificador 
		return true;		//para todo siempre es TRUE

	int maxIzquierdaDeP = vector.at(p);
	int minDerechaDeP = vector.at(p+1);


    for(int i = 0; i <= p; i++){		//Busqueda del máximo a la izquierda de p

    	if(vector.at(i) > maxIzquierdaDeP)
    		maxIzquierdaDeP = vector.at(i); 

      }

    for(int j = p + 1; j < longVector; j++){	//Busqueda del mínimo a la derecha de p

    	if(vector.at(j) < minDerechaDeP)
    		minDerechaDeP = vector.at(j); 
      }

      if(maxIzquierdaDeP >= minDerechaDeP)	//En la especificación es menor estricto, por lo tanto si son iguales b es FALSE
      	return false;
      else 
      	return true;
}




// Resuelve un caso de prueba, leyendo de la entrada la
// configuracioón, y escribiendo la respuesta
void resuelveCaso() {
    // leer los datos de la entrada
    	bool b;
        int longitudVector, posicionVector;
        cin >> longitudVector;
        cin >> posicionVector;
        vector<int> v(longitudVector);
       

        for(int& numero : v)
            cin >> numero;

    	b = resolver(v, longitudVector, posicionVector);

    // escribir sol

    	if (b)
			cout << "SI" << "\n";
		else
			cout << "NO" << "\n";
}

int main() {
    // Para la entrada por fichero.
    // Comentar para acepta el reto
    #ifndef DOMJUDGE
     std::ifstream in("/Users/Sergio/Library/Mobile Documents/com~apple~CloudDocs/Practicas/EDA/JuezEDA/Problema2/datos2.txt");
     auto cinbuf = std::cin.rdbuf(in.rdbuf()); //save old buf and redirect std::cin to casos.txt
     #endif 
    
    
    int numCasos;
    cin >> numCasos;
    for (int i = 0; i < numCasos; ++i)
        resuelveCaso();

    
    // Para restablecer entrada. Comentar para acepta el reto
     #ifndef DOMJUDGE // para dejar todo como estaba al principio
     std::cin.rdbuf(cinbuf);
     //system("PAUSE");
     #endif
    
    return 0;
}
