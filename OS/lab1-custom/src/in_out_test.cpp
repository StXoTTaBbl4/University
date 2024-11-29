#include <iostream>
#include <string>

using namespace std;

int main() {
    cout << "Welcome to IO test 'Echo'. Type anything to receive echo!\n";
    while (true) {
        string input;
        getline(cin, input);
        if (input == "  ") {
            break;
        }
        cout << input << endl;
    }
    return 0;
}