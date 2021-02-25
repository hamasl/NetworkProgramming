FROM gcc:10.2

RUN apt-get update -y

COPY ./cpp_code /usr/cpp_code

WORKDIR /usr/cpp_code

RUN g++ -std=c++20 main.cpp -o main

CMD ["./main"]
