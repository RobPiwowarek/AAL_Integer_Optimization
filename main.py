#!/usr/bin/python3
import numpy as np
from numpy import float64
import sys


def quadratic_form(A, B, c, X):
    return (0.5 * (np.transpose(X) * A * X) + np.transpose(B) * X + c)[0, 0]


def minimum(A, B, c=None):
    # return -1 * A.I * B
    return np.linalg.solve(A, -B)


def minor(arr, i, j):
    # ith row, jth column removed
    return np.matrix(arr[np.array(list(range(i)) + list(range(i + 1, arr.shape[0])))[:, np.newaxis],
                         np.array(list(range(j)) + list(range(j + 1, arr.shape[1])))])


def chop_B(A, B, x0):
    result = B.copy()[1:]
    for i in range(len(B) - 1):
        result[i] += (A[i + 1, 0] + A[0, i + 1]) * 0.5 * x0
    return result


def iteration(A, B, c, X):
    x0 = X[0, 0]

    B_p = chop_B(A, B, x0)
    A_p = minor(A, 0, 0)
    c_p = (A[0, 0] * 0.5 * (x0 ** 2) + B[0] * x0)[0, 0] + c
    X_p = minimum(A_p, B_p, c_p)

    print("Q:\n", A_p)
    print("B':\n", B_p)
    print("X':\n", X_p)
    print("c:\n", c_p)
    func_val = quadratic_form(A_p, B_p, c_p, X_p)
    print("Value: ", func_val)
    return A_p, B_p, c_p, X_p


def main():
    with open(sys.argv[1], "r") as f:
        a = f.read()
    with open(sys.argv[2], "r") as f:
        b = f.read()
    c = sys.argv[3]

    a = [i.split(" ") for i in a.split("\n")]
    if a[len(a) - 1] == ['']:
        a.pop(len(a) - 1)
    b = b.split("\n")
    if b[len(b) - 1] == '':
        b.pop(len(b) - 1)

    for i in a:
        for j in range(len(i)):
            i[j] = float64(i[j])
    for i in range(len(b)):
        b[i] = float64(b[i])

    A = np.matrix(a)
    B = np.matrix(b).transpose()
    c = float64(c)

    print("===========================================")
    print("A:\n", A)
    print("B:\n", B)
    print("c: ", c)

    # x = input("x: ")
    # x.replace("\n", ";")
    # X = np.matrix(x).transpose()
    #
    # func_val = quadratic_form(A, B, c, X)
    # print("Value for X:", func_val)
    minimum = -1 * np.linalg.inv(A) * B
    print("===========================================")
    print("Minimum:\n", minimum)
    func_val = quadratic_form(A, B, c, minimum)
    print("Value: ", func_val)

    X = minimum





    X[0] = -73.000000
    X[1] = -94.000000
    X[2] = 212.000000

    func_val = quadratic_form(A, B, c, X)
    print("===========================================")
    print("Int Value: ", func_val)

    # X = minimum

    size = len(B)
    for i in range(size - 1):
       print("===========================================")
       print(i);
       A, B, c, X = iteration(A, B, c, X)


if __name__ == "__main__":
    main()