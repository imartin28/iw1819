// Nombre del alumno: Irene Martin Berlanga
// Usuario del Juez: EDA35


#include <iostream>
#include <iomanip>
#include <fstream>
#include <vector>
#include "bintree_eda.h"
#include <sstream>
//==========================>   EJERCICIO 24 <==========================//

/*COSTE:
 
 
 */

template<typename T>
bintree<T> reconstruirArbol(std::vector<T>const& v_preorden, std::vector<T>const& v_inorden, int inorden_ini, int inorden_fin, int preorden_ini, int preorden_fin){
    
    if(preorden_ini > preorden_fin){
        return {};
    }else if(preorden_ini == preorden_fin){
        return {v_preorden[preorden_ini]};
        
    }else {
        T raiz = v_preorden[preorden_ini];
        
        int inorden_izq_ini;
        int inorden_izq_fin = 0;
        
        int inorden_der_ini;
        int inorden_der_fin;
       
        int preorden_izq_ini;
        int preorden_izq_fin;
        
        int preorden_der_ini;
        int preorden_der_fin;
       
        
        
        
        int indice_raiz = inorden_ini;
       
        while(v_inorden[indice_raiz] != raiz){
            ++indice_raiz;
        }
        
        inorden_izq_fin = indice_raiz - 1;
        inorden_izq_ini = inorden_ini;
        
        preorden_izq_ini = preorden_ini + 1;
        preorden_izq_fin = preorden_ini + inorden_izq_fin - inorden_izq_ini + 1;
        
        
        inorden_der_ini = indice_raiz + 1;
        inorden_der_fin = inorden_fin;
        
        preorden_der_ini = preorden_izq_fin + 1;
        preorden_der_fin = preorden_fin;
        
        
        bintree<T> arbol_izq = reconstruirArbol(v_preorden, v_inorden, inorden_izq_ini, inorden_izq_fin, preorden_izq_ini, preorden_izq_fin );
        
        bintree<T> arbol_der = reconstruirArbol(v_preorden, v_inorden, inorden_der_ini, inorden_der_fin, preorden_der_ini, preorden_der_fin);
        
        
        return bintree<T>(arbol_izq, raiz, arbol_der);
    }
    
}

// Resuelve un caso de prueba, leyendo de la entrada la
// configuración, y escribiendo la respuesta
bool resuelveCaso() {
    // leer los datos de la entrada
    std::vector<int> v_preorden;
    std::vector<int> v_inorden;
    
    
    std::string linea;
    std::getline(std::cin, linea);
    std::stringstream ss(linea);
    int numero;
    while(ss >> numero){
        v_preorden.push_back(numero);
    }
    
    std::string linea2;
    std::getline(std::cin, linea2);
    std::stringstream ss2(linea2);
   
    while(ss2 >> numero){
        v_inorden.push_back(numero);
    }
    
    
    if (! std::cin)
        return false;
    
    bintree<int> arbol = reconstruirArbol(v_preorden, v_inorden, 0, v_inorden.size()-1, 0, v_preorden.size() -1);
    
    // escribir sol
    
    std::vector<int> v_postorden = arbol.postorder();
    
    for(int i = 0; i < v_postorden.size(); ++i){
        std::cout << v_postorden[i] << ' ';
    }
    std::cout << std::endl;
    
    return true;
    
}

int main() {
    // Para la entrada por fichero.
    // Comentar para acepta el reto
#ifndef DOMJUDGE
    std::ifstream in("/Users/IRENE/Desktop/EDA_2/txt/eda24.txt");
    auto cinbuf = std::cin.rdbuf(in.rdbuf()); //save old buf and redirect std::cin to casos.txt
#endif
    
    
    while (resuelveCaso())
        ;
    
    
    // Para restablecer entrada. Comentar para acepta el reto
#ifndef DOMJUDGE // para dejar todo como estaba al principio
    std::cin.rdbuf(cinbuf);
    system("PAUSE");
#endif
    
    return 0;
}
