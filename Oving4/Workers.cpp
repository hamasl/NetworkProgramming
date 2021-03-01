#include <iostream>
#include <list>
#include <functional>
#include <vector>
#include <thread>
#include <mutex>
#include <condition_variable>
#include <atomic>

using namespace std;

class Workers
{
private:
    int n;
    list<function<void()>> tasks;
    mutex tasksLock;
    vector<thread> threads;
    bool waitForTasks;
    condition_variable cv;
    atomic<bool> keepGoing;
    //Have to add this so that the stop method waits until tasks posted by post-timeout are able to complete aswell
    atomic<long> tasksToBeDone;

public:
    Workers(int numberOfThreads)
    {
        n = numberOfThreads;
    }

    void start()
    {
        tasksToBeDone.operator=(0);
        keepGoing.operator=(true);
        for (int i = 0; i < n; ++i)
        {
            threads.emplace_back([this] {
                while (keepGoing)
                {
                    function<void()> task;
                    {
                        unique_lock<mutex> lock(this->tasksLock);
                        while (tasks.empty() && keepGoing)
                            cv.wait(lock);
                        if (keepGoing)
                        {
                            task = *tasks.begin();
                            tasks.pop_front();
                            tasksToBeDone.operator--();
                        }
                    }
                    if (task)
                        task();
                }
            });
        }
    }

    void post(function<void()> task)
    {
        tasksToBeDone.operator++();
        unique_lock<mutex>(this->tasksLock);
        tasks.emplace_back(task);
        cv.notify_one();
    }

    void post_timeout(function<void()> task, long milliseconds)
    {
        tasksToBeDone.operator++();
        //Not using mutex lock on threads vector since this method is only supposed to be called from the singlethreaded main method
        threads.emplace_back([this, task, milliseconds] {
            this_thread::sleep_for(chrono::milliseconds(milliseconds));
            unique_lock<mutex>(this->tasksLock);
            tasks.emplace_back(task);
            cv.notify_one();
        });
    }

    void stop()
    {
        threads.emplace_back([this] {
            while (keepGoing)
            {
                unique_lock<mutex>(this->tasksLock);
                if (tasks.empty() && tasksToBeDone == 0)
                {
                    keepGoing.operator=(false);
                    cv.notify_all();
                }
                else
                    cv.notify_one();
            }
        });
    }

    void join()
    {
        //Using threads.size() since post_timeout adds a new thread to the vector
        for (int i = 0; i < threads.size(); ++i)
        {
            threads[i].join();
        }
    }

    ~Workers()
    {
        unique_lock<mutex>(this->tasksLock);
        tasks.clear();
        threads.clear();
    }
};

int main()
{
    //Printing out WT for Worker Threads and EV for Event loop

    Workers worker_threads(4);
    Workers event_loop(1);

    worker_threads.start();
    event_loop.start();

    worker_threads.post_timeout([] {
        cout << "WT Delayed Task A 5000ms" << endl;
    },
                                5000);

    worker_threads.post([] {
        cout << "WT Task B" << endl;
    });

    worker_threads.post([] {
        cout << "WT Task C" << endl;
    });

    event_loop.post_timeout([] {
        cout << "EV delayed Task D 2000ms" << endl;
    },
                            2000);

    event_loop.post_timeout([] {
        cout << "EV delayed Task E 1000ms" << endl;
    },
                            1000);

    event_loop.post([] {
        cout << "EV Task F" << endl;
    });

    event_loop.post([] {
        cout << "EV Task G" << endl;
    });

    worker_threads.stop();
    event_loop.stop();

    worker_threads.join();
    event_loop.join();

    return 0;
}
