# Gustavo Paulo - Questão 01
clc; clear all; close all;

N = 100000; 
X1 = rand(1, N) < 1/2;
X2 = rand(1, N) < 1/2;
X3 = rand(1, N) < 1/2;

# A
Y1 = X1;
Y2 = X1 .* X2;
Y3 = X1 .* X2 .* X3;
muY_sim = mean([Y1' Y2' Y3'])'
muY_teo = [1/2; 1/4; 1/8]
covY_sim = cov([Y1' Y2' Y3'])
covY_teo = [1/4 1/8 1/16; 1/8 3/16 3/32; 1/16 3/32 7/64]

# B

Z1 = Y1;
Z2 = Y1 + Y2;
Z3 = Y1 + Y2 + Y3;
muZ_sim = mean([Z1' Z2' Z3'])'
muZ_teo = [1/2; 3/4; 7/8]
covZ_sim = cov([Z1' Z2' Z3'])
covZ_teo = [1/4 3/8 7/16; 3/8 11/16 27/32; 7/16 27/32 71/64]