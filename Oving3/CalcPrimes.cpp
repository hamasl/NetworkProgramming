#include <algorithm>
#include <mutex>
#include <thread>
#include <list>
#include <iostream>
#include <vector>
#include <cmath>

using namespace std;

bool isPrime(int number){
    if (number == 2) return true;
    if (number <= 1 || number % 2 == 0) return false;
    int squareRoot = (int)sqrt(number);
    //Adding two to avoid all even numbers since they are divisible by two anyways
    //Only going to square root of number since any higher factor, would need to be multiplied by a lesser factor, and would therefore have already been found
    for (int i = 3; i <= squareRoot; i+= 2){
        if(number % i == 0) return false;
    }
    return true;
}


int main(){

    int min,max, numberOfThreads;
    cout << "Enter the smallest number to be searched from: ";
    cin >> min;
    cout << "Enter the largest number to be searched to: ";
    cin >> max;
    cout << "Enter number of threads to be used: ";
    cin >> numberOfThreads;
    cout << endl;

    mutex primeMutex;
    vector<int> primeNums;

    vector<thread> threads;

    //No primes less than 2 therefore only searching from two and uppwards
    int threadMin = (min < 2) ? 2 : min;
    int step = (max-threadMin)/numberOfThreads;
    int threadMax = step+threadMin;
    for (int i = 0; i < numberOfThreads; i++) {
        //Not passing in &threadMin and &threadMax, since we want every thread to have its own copy
        threads.emplace_back(thread([threadMin, threadMax, &primeMutex, &primeNums]{
            //Incrementing like that to force j to skip every even number except two
            for (int j = threadMin; j < threadMax; (j%2 == 0 || j == 1)?++j:j+=2) {
                if (isPrime(j)){
                    unique_lock<mutex> lock(primeMutex);
                    primeNums.emplace_back(j);
                }
            }
        }));
        threadMin = threadMax;
        threadMax = (i == numberOfThreads -2) ? (max+1) : threadMax+step;
    }

    for (int i = 0; i < numberOfThreads; ++i) {
        threads[i].join();
    }
    threads.clear();

    sort(primeNums.begin(), primeNums.end());

    for (int i = 0; i < primeNums.size(); ++i) {
        cout << primeNums[i] << "\n";
    }
    cout << endl;
    primeNums.clear();

    return 0;
}

