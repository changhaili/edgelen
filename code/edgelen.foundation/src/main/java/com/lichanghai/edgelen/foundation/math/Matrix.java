package com.lichanghai.edgelen.foundation.math;

public class Matrix {

	public Matrix(int row, int col) {
		assert (row > 0 && col > 0) : "row or column of matrix if illegal";
		this.row = row;
		this.col = col;
		elem = new double[row][col];
	}

	public Matrix(int row, int col, double init) {
		assert (row > 0 && col > 0) : "row or column of matrix if illegal";
		this.row = row;
		this.col = col;
		elem = new double[row][col];
		for (int i = 0; i < row; ++i) {
			for (int j = 0; j < col; ++j) {
				setElem(i, j, init);
			}
		}
	}

	public Matrix(int row, int col, double... ms) {

		this.row = row;
		this.col = col;

		this.elem = new double[row][col];

		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++)

				this.elem[i][j] = ms[i * col + j];
		}
	}

	public Matrix(double[][] m) {
		row = m.length;
		col = m[0].length;
		elem = new double[row][col];
		for (int i = 0; i < row; ++i) {
			if (m[i].length != col) {
				throw new IllegalArgumentException("All rows must have the same length");
			}
		}
		this.elem = m;
	}

	public double[][] getArray() {
		return elem;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public double getElem(int r, int c) {
		assert (r >= 0 && c >= 0) : "arguments of row or column are negative value";
		assert (r < row && c < col) : "arguments are out of the range of this matrix";
		return elem[r][c];

	}

	public void setElem(int r, int c, double elem) {
		assert (r >= 0 && c >= 0) : "arguments of row or column are negative value";
		assert (r < row && c < col) : "arguments are out of the range of this matrix";
		this.elem[r][c] = elem;
	}

	public Matrix add(Matrix mat) {
		assert (mat.getRow() == row && mat.getCol() == col) : "can't add two matrix with different row or col";
		Matrix m = new Matrix(row, col);
		for (int i = 0; i < row; ++i) {
			for (int j = 0; j < col; ++j) {
				m.setElem(i, j, mat.getElem(i, j) + elem[i][j]);
			}
		}
		return m;
	}

	public Matrix sub(Matrix mat) {
		assert (mat.getRow() == row && mat.getCol() == col) : "can't substract two matrix with different row or col";
		Matrix m = new Matrix(row, col);
		for (int i = 0; i < row; ++i) {
			for (int j = 0; j < col; ++j) {
				m.setElem(i, j, elem[i][j] - mat.getElem(i, j));
			}
		}
		return m;
	}

	public Matrix transform() {
		Matrix m = new Matrix(col, row);
		for (int i = 0; i < row; ++i) {
			for (int j = 0; j < col; ++j) {
				m.setElem(j, i, elem[i][j]);
			}
		}
		return m;
	}

	// used by inverse
	private void choice_the_main(Matrix m, int k, int is[], int js[]) {
		double fMax = 0.0f;
		for (int i = k; i < m.getRow(); ++i) {
			for (int j = k; j < m.getCol(); ++j) {
				double f = Math.abs(m.getElem(i, j));
				if (f > fMax) {
					fMax = f;
					is[k] = i;
					js[k] = j;
				}
			}
		}
	}

	// used by inverse
	private void swap(Matrix m, int i, int j, int l, int k) {
		double temp = m.getElem(i, j);
		m.setElem(i, j, m.getElem(l, k));
		m.setElem(l, k, temp);
	}

	// used by inverse
	private void inverse_course(Matrix m, int k) {
		// three step
		m.setElem(k, k, 1.0f / m.getElem(k, k));
		for (int j = 0; j < m.getCol(); ++j)
			if (j != k)
				m.setElem(k, j, m.getElem(k, j) * m.getElem(k, k));
		// four step
		for (int i = 0; i < m.getRow(); ++i) {
			if (i != k) {
				for (int j = 0; j < m.getCol(); ++j) {
					if (j != k)
						m.setElem(i, j, m.getElem(i, j) - m.getElem(i, k) * m.getElem(k, j));
				}
			}
		}
		// five step
		for (int i = 0; i < m.getRow(); ++i) {
			if (i != k)
				m.setElem(i, k, -m.getElem(i, k) * m.getElem(k, k));
		}
	}

	//
	public Matrix inverse() {
		assert (row == col) : "row is not identical to col";
		Matrix m = new Matrix(row, col);
		// gaussian-jordan
		this.copyInto(m);

		int is[] = new int[row];
		int js[] = new int[col];

		for (int k = 0; k < row; ++k) {
			// one step
			choice_the_main(m, k, is, js);
			// two step
			if (is[k] != k) {
				for (int j = 0; j < col; ++j)
					swap(m, k, j, is[k], j);
			}
			if (js[k] != k) {
				for (int i = 0; i < row; ++i)
					swap(m, i, k, i, js[k]);
			}
			inverse_course(m, k);
		}
		// six step
		for (int k = row - 1; k >= 0; --k) {
			if (js[k] != k) {
				for (int j = 0; j < col; ++j)
					swap(m, k, j, js[k], j);
			}
			if (is[k] != k) {
				for (int i = 0; i < row; ++i)
					swap(m, i, k, i, is[k]);
			}
		}
		return m;
	}

	public Matrix mult(Matrix mat) {
		assert (col == mat.getRow()) : "column of matrix is not identical to the row of matrix which will be multipled";
		Matrix m = new Matrix(row, mat.getCol());
		for (int i = 0; i < row; ++i)
			for (int j = 0; j < mat.getCol(); ++j) {
				double sum = 0.0f;
				for (int k = 0; k < col; ++k) {
					sum += elem[i][k] * mat.getElem(k, j);
				}
				m.setElem(i, j, sum);
			}
		return m;
	}

	public Matrix mult(double d) {
		Matrix m = new Matrix(row, col);
		for (int i = 0; i < row; ++i) {
			for (int j = 0; j < col; ++j) {
				m.setElem(i, j, elem[i][j] * d);
			}
		}
		return m;
	}

	public void copyInto(Matrix mat) {
		assert (row == mat.getRow() && col == mat.getCol()) : "row and col are not identical";
		for (int i = 0; i < row; ++i) {
			for (int j = 0; j < col; ++j) {
				mat.setElem(i, j, elem[i][j]);
			}
		}
	}

	public static void eig(Matrix m, Matrix eigValue, Matrix eigVec) {
		Eig e = new Eig(m);
		e.getD().copyInto(eigValue);
		e.getV().copyInto(eigVec);
	}

	public static void eig(Matrix m, Matrix eigValue) {
		int row = m.getRow();
		int col = m.getCol();
		assert (row == col) : "row and col must be identical";
		assert (eigValue.getRow() == row && eigValue.getCol() == 1) : "illegal argument";
		double a[][] = new double[row + 1][col + 1];
		double wr[] = new double[row + 1];
		double wi[] = new double[row + 1];
		for (int i = 1; i < row + 1; ++i) {
			for (int j = 1; j < col + 1; ++j)
				a[i][j] = m.getElem(i - 1, j - 1);
		}

		balanc(a, row);
		elmhes(a, row);
		hqr(a, row, wr, wi);
		for (int i = 0; i < row; ++i) {
			eigValue.setElem(i, 0, wr[i + 1]);
		}
	}

	// 逆矩阵解线性方程组
	public static void solve0(Matrix A, Matrix R, Matrix key) {
		// 利用逆矩阵求解线性方程组
		assert (A.getRow() == key.getRow()) : "argumented matrix is wrong";// 结果矩阵为向量形式
		//
		Matrix inv = A.inverse();
		Matrix s = inv.mult(R);
		for (int i = 0; i < key.getRow(); ++i) {
			key.setElem(i, 0, s.getElem(i, 0));
		}
	}

	// 列主元消去法解线性方程组
	public static void solve1(Matrix A, Matrix R, Matrix key) {
		// 列主元高斯消去法解线性方程组
		Matrix mat = new Matrix(A.getRow(), A.getCol() + 1);
		for (int i = 0; i < A.getRow(); ++i) {
			for (int j = 0; j < A.getCol(); ++j) {
				mat.setElem(i, j, A.getElem(i, j));
			}
			mat.setElem(i, A.getCol(), R.getElem(i, 0));
		}
		assert (mat.getRow() == key.getRow()) : "argumented matrix is wrong";// 结果矩阵为向量形式
		assert ((mat.getRow() + 1) == mat.getCol()) : "argumented matrix is wrong";// 增广矩阵

		double[][] m = mat.getArray();
		double[][] x = key.getArray();
		int row = mat.getRow(), col = mat.getCol();

		for (int i = 0; i < row; ++i)
			x[i][0] = 0;
		// 消元
		for (int i = 0; i < row - 1; ++i) {
			// 选主元
			double fMax = Math.abs(m[i][i]);
			int k = i;
			for (int j = i + 1; j < row; ++j) {
				if (Math.abs(m[j][i]) > fMax) {
					fMax = Math.abs(m[j][i]);
					k = j;
				}
			}

			if (k != i) {
				//
				for (int j = i; j < col; ++j) {
					double[] t = swap(m[i][j], m[k][j]);
					m[i][j] = t[0];
					m[k][j] = t[1];
				}
			}
			// 消元
			for (int j = i + 1; j < row; ++j) {
				if (m[i][i] == 0.0f)
					return;
				double l = m[j][i] / m[i][i];

				m[j][i] = 0.0f;
				for (k = i + 1; k < col; ++k)
					m[j][k] -= l * m[i][k];
			}
		}

		for (int i = row - 1; i >= 0; --i) {
			double l = m[i][col - 1];
			for (int j = i + 1; j < col - 1; ++j)
				l -= m[i][j] * x[j][0];
			if (m[i][i] != 0.0f)
				x[i][0] = l / m[i][i];
			else
				return;
		}
	}

	// LU分解解线性方程组
	public static void solve2(Matrix A, Matrix R, Matrix key) {
		// LU分解解线性方程组
		Matrix mat = new Matrix(A.getRow(), A.getCol() + 1);
		for (int i = 0; i < A.getRow(); ++i) {
			for (int j = 0; j < A.getCol(); ++j) {
				mat.setElem(i, j, A.getElem(i, j));
			}
			mat.setElem(i, A.getCol(), R.getElem(i, 0));
		}
		assert (mat.getRow() == key.getRow()) : "argumented matrix is wrong";// 结果矩阵为向量形式
		assert ((mat.getRow() + 1) == mat.getCol()) : "argumented matrix is wrong";// 增广矩阵
		double[][] m = mat.getArray();
		double[][] x = key.getArray();
		int i, j, k;
		double u, l;
		// 下三角
		// 上三角
		for (i = 0; i < mat.getRow(); i++) {
			// 行
			for (j = i; j < mat.getCol(); j++)// i*j
			{
				u = 0;
				for (k = 0; k < i; k++)
					u += m[i][k] * m[k][j];
				m[i][j] = m[i][j] - u;
			}

			// 列
			for (j = i + 1; j < mat.getRow(); j++)// j*i
			{
				if (m[i][i] == 0.0f)
					return;
				l = 0;
				for (k = 0; k < i; k++)
					l += m[j][k] * m[k][i];
				m[j][i] = (m[j][i] - l) / m[i][i];
			}
		}
		// 回代
		for (i = mat.getRow() - 1; i >= 0; i--) {
			l = 0;
			for (j = i + 1; j < mat.getRow(); j++)
				l += x[j][0] * m[i][j];
			x[i][0] = (m[i][mat.getCol() - 1] - l) / m[i][i];
		}
	}

	//
	private static double RADIX = 2.0f;

	private static void balanc(double a[][], int n) {
		int last, j, i;
		double s, r, g, f, c, sqrdx;
		sqrdx = RADIX * RADIX;
		last = 0;
		while (last == 0) {
			last = 1;
			for (i = 1; i <= n; i++) {
				r = c = 0.0;
				for (j = 1; j <= n; j++)
					if (j != i) {
						c += Math.abs(a[j][i]);
						r += Math.abs(a[i][j]);
					}
				if (c != 0 && r != 0) {
					g = r / RADIX;
					f = 1.0;
					s = c + r;
					while (c < g) {
						f *= RADIX;
						c *= sqrdx;
					}
					g = r * RADIX;
					while (c > g) {
						f /= RADIX;
						c /= sqrdx;
					}
					if ((c + r) / f < 0.95 * s) {
						last = 0;
						g = 1.0 / f;
						for (j = 1; j <= n; j++)
							a[i][j] *= g;
						for (j = 1; j <= n; j++)
							a[j][i] *= f;
					}
				}
			}
		}
	}

	private static double[] swap(double a, double b) {
		double temp[] = new double[2];
		temp[0] = b;
		temp[1] = a;
		return temp;
	}

	private static void elmhes(double a[][], int n) {
		int m, j, i;
		double y, x;

		for (m = 2; m < n; m++) {
			x = 0.0;
			i = m;
			for (j = m; j <= n; j++) {
				if (Math.abs(a[j][m - 1]) > Math.abs(x)) {
					x = a[j][m - 1];
					i = j;
				}
			}
			if (i != m) {
				for (j = m - 1; j <= n; j++) {
					double s[] = swap(a[i][j], a[m][j]);
					a[i][j] = s[0];
					a[m][j] = s[1];
				}
				for (j = 1; j <= n; j++) {
					double s[] = swap(a[j][i], a[j][m]);
					a[j][i] = s[0];
					a[j][m] = s[1];
				}
			}
			if (x != 0) {
				for (i = m + 1; i <= n; i++) {
					if ((y = a[i][m - 1]) != 0.0f) {
						y /= x;
						a[i][m - 1] = y;
						for (j = m; j <= n; j++)
							a[i][j] -= y * a[m][j];
						for (j = 1; j <= n; j++)
							a[j][m] += y * a[j][i];
					}
				}
			}
		}
	}

	private static double sign(double a, double b) {
		return b > 0 ? Math.abs(a) : -Math.abs(a);
	}

	public static void hqr(double a[][], int n, double wr[], double wi[]) {
		int nn, m, l, k, j, its, i, mmin;
		double z, y, x, w, v, u, t, s, r = 0, q = 0, p = 0, anorm;

		anorm = Math.abs(a[1][1]);
		for (i = 2; i <= n; i++)
			for (j = (i - 1); j <= n; j++)
				anorm += Math.abs(a[i][j]);
		nn = n;
		t = 0.0;
		while (nn >= 1) {
			its = 0;
			do {
				for (l = nn; l >= 2; l--) {
					s = Math.abs(a[l - 1][l - 1]) + Math.abs(a[l][l]);
					if (s == 0.0)
						s = anorm;
					if (Math.abs(a[l][l - 1]) + s == s)
						break;
				}
				x = a[nn][nn];
				if (l == nn) {
					wr[nn] = x + t;
					wi[nn--] = 0.0;
				} else {
					y = a[nn - 1][nn - 1];
					w = a[nn][nn - 1] * a[nn - 1][nn];
					if (l == (nn - 1)) {
						p = 0.5 * (y - x);
						q = p * p + w;
						z = Math.sqrt(Math.abs(q));
						x += t;
						if (q >= 0.0) {
							z = p + sign(z, p);
							wr[nn - 1] = wr[nn] = x + z;
							if (z != 0)
								wr[nn] = x - w / z;
							wi[nn - 1] = wi[nn] = 0.0;
						} else {
							wr[nn - 1] = wr[nn] = x + p;
							wi[nn - 1] = -(wi[nn] = z);
						}
						nn -= 2;
					} else {
						// if (its == 30)
						// System.out.println("Too many iterations in HQR");
						if (its == 10 || its == 20) {
							t += x;
							for (i = 1; i <= nn; i++)
								a[i][i] -= x;
							s = Math.abs(a[nn][nn - 1]) + Math.abs(a[nn - 1][nn - 2]);
							y = x = 0.75 * s;
							w = -0.4375 * s * s;
						}
						++its;
						for (m = (nn - 2); m >= l; m--) {
							z = a[m][m];
							r = x - z;
							s = y - z;
							p = (r * s - w) / a[m + 1][m] + a[m][m + 1];
							q = a[m + 1][m + 1] - z - r - s;
							r = a[m + 2][m + 1];
							s = Math.abs(p) + Math.abs(q) + Math.abs(r);
							p /= s;
							q /= s;
							r /= s;
							if (m == l)
								break;
							u = Math.abs(a[m][m - 1]) * (Math.abs(q) + Math.abs(r));
							v = Math.abs(p) * (Math.abs(a[m - 1][m - 1]) + Math.abs(z) + Math.abs(a[m + 1][m + 1]));
							if (u + v == v)
								break;
						}
						for (i = m + 2; i <= nn; i++) {
							a[i][i - 2] = 0.0;
							if (i != (m + 2))
								a[i][i - 3] = 0.0;
						}
						for (k = m; k <= nn - 1; k++) {
							if (k != m) {
								p = a[k][k - 1];
								q = a[k + 1][k - 1];
								r = 0.0;
								if (k != (nn - 1))
									r = a[k + 2][k - 1];
								if ((x = Math.abs(p) + Math.abs(q) + Math.abs(r)) != 0) {
									p /= x;
									q /= x;
									r /= x;
								}
							}
							if ((s = sign(Math.sqrt(p * p + q * q + r * r), p)) != 0) {
								if (k == m) {
									if (l != m)
										a[k][k - 1] = -a[k][k - 1];
								} else
									a[k][k - 1] = -s * x;
								p += s;
								x = p / s;
								y = q / s;
								z = r / s;
								q /= p;
								r /= p;
								for (j = k; j <= nn; j++) {
									p = a[k][j] + q * a[k + 1][j];
									if (k != (nn - 1)) {
										p += r * a[k + 2][j];
										a[k + 2][j] -= p * z;
									}
									a[k + 1][j] -= p * y;
									a[k][j] -= p * x;
								}
								mmin = nn < k + 3 ? nn : k + 3;
								for (i = l; i <= mmin; i++) {
									p = x * a[i][k] + y * a[i][k + 1];
									if (k != (nn - 1)) {
										p += z * a[i][k + 2];
										a[i][k + 2] -= p * r;
									}
									a[i][k + 1] -= p * q;
									a[i][k] -= p;
								}
							}
						}
					}
				}
			} while (l < nn - 1);
		}
	}

	private static class Maths {
		public static double hypot(double a, double b) {
			double r;
			if (Math.abs(a) > Math.abs(b)) {
				r = b / a;
				r = Math.abs(a) * Math.sqrt(1 + r * r);
			} else if (b != 0) {
				r = a / b;
				r = Math.abs(b) * Math.sqrt(1 + r * r);
			} else {
				r = 0.0;
			}
			return r;
		}
	}

	/** internal class for eig method **/
	private static class Eig {
		private boolean issymmetric;
		private int n;
		private double[][] V, H;
		private double[] d, e, ort;
		private transient double cdivr, cdivi;

		private void tred2() {
			for (int j = 0; j < n; j++) {
				d[j] = V[n - 1][j];
			}
			for (int i = n - 1; i > 0; i--) {
				double scale = 0.0;
				double h = 0.0;
				for (int k = 0; k < i; k++) {
					scale = scale + Math.abs(d[k]);
				}
				if (scale == 0.0) {
					e[i] = d[i - 1];
					for (int j = 0; j < i; j++) {
						d[j] = V[i - 1][j];
						V[i][j] = 0.0;
						V[j][i] = 0.0;
					}
				} else {
					for (int k = 0; k < i; k++) {
						d[k] /= scale;
						h += d[k] * d[k];
					}
					double f = d[i - 1];
					double g = Math.sqrt(h);
					if (f > 0) {
						g = -g;
					}
					e[i] = scale * g;
					h = h - f * g;
					d[i - 1] = f - g;
					for (int j = 0; j < i; j++) {
						e[j] = 0.0;
					}
					for (int j = 0; j < i; j++) {
						f = d[j];
						V[j][i] = f;
						g = e[j] + V[j][j] * f;
						for (int k = j + 1; k <= i - 1; k++) {
							g += V[k][j] * d[k];
							e[k] += V[k][j] * f;
						}
						e[j] = g;
					}
					f = 0.0;
					for (int j = 0; j < i; j++) {
						e[j] /= h;
						f += e[j] * d[j];
					}
					double hh = f / (h + h);
					for (int j = 0; j < i; j++) {
						e[j] -= hh * d[j];
					}
					for (int j = 0; j < i; j++) {
						f = d[j];
						g = e[j];
						for (int k = j; k <= i - 1; k++) {
							V[k][j] -= (f * e[k] + g * d[k]);
						}
						d[j] = V[i - 1][j];
						V[i][j] = 0.0;
					}
				}
				d[i] = h;
			}
			for (int i = 0; i < n - 1; i++) {
				V[n - 1][i] = V[i][i];
				V[i][i] = 1.0;
				double h = d[i + 1];
				if (h != 0.0) {
					for (int k = 0; k <= i; k++) {
						d[k] = V[k][i + 1] / h;
					}
					for (int j = 0; j <= i; j++) {
						double g = 0.0;
						for (int k = 0; k <= i; k++) {
							g += V[k][i + 1] * V[k][j];
						}
						for (int k = 0; k <= i; k++) {
							V[k][j] -= g * d[k];
						}
					}
				}
				for (int k = 0; k <= i; k++) {
					V[k][i + 1] = 0.0;
				}
			}
			for (int j = 0; j < n; j++) {
				d[j] = V[n - 1][j];
				V[n - 1][j] = 0.0;
			}
			V[n - 1][n - 1] = 1.0;
			e[0] = 0.0;
		}

		private void tql2() {
			for (int i = 1; i < n; i++) {
				e[i - 1] = e[i];
			}
			e[n - 1] = 0.0;

			double f = 0.0;
			double tst1 = 0.0;
			double eps = Math.pow(2.0, -52.0);
			for (int l = 0; l < n; l++) {
				tst1 = Math.max(tst1, Math.abs(d[l]) + Math.abs(e[l]));
				int m = l;
				while (m < n) {
					if (Math.abs(e[m]) <= eps * tst1) {
						break;
					}
					m++;
				}
				if (m > l) {
					int iter = 0;
					do {
						iter = iter + 1;
						double g = d[l];
						double p = (d[l + 1] - g) / (2.0 * e[l]);
						double r = Maths.hypot(p, 1.0);
						if (p < 0) {
							r = -r;
						}
						d[l] = e[l] / (p + r);
						d[l + 1] = e[l] * (p + r);
						double dl1 = d[l + 1];
						double h = g - d[l];
						for (int i = l + 2; i < n; i++) {
							d[i] -= h;
						}
						f = f + h;
						p = d[m];
						double c = 1.0;
						double c2 = c;
						double c3 = c;
						double el1 = e[l + 1];
						double s = 0.0;
						double s2 = 0.0;
						for (int i = m - 1; i >= l; i--) {
							c3 = c2;
							c2 = c;
							s2 = s;
							g = c * e[i];
							h = c * p;
							r = Maths.hypot(p, e[i]);
							e[i + 1] = s * r;
							s = e[i] / r;
							c = p / r;
							p = c * d[i] - s * g;
							d[i + 1] = h + s * (c * g + s * d[i]);
							for (int k = 0; k < n; k++) {
								h = V[k][i + 1];
								V[k][i + 1] = s * V[k][i] + c * h;
								V[k][i] = c * V[k][i] - s * h;
							}
						}
						p = -s * s2 * c3 * el1 * e[l] / dl1;
						e[l] = s * p;
						d[l] = c * p;
					} while (Math.abs(e[l]) > eps * tst1);
				}
				d[l] = d[l] + f;
				e[l] = 0.0;
			}
			for (int i = 0; i < n - 1; i++) {
				int k = i;
				double p = d[i];
				for (int j = i + 1; j < n; j++) {
					if (d[j] < p) {
						k = j;
						p = d[j];
					}
				}
				if (k != i) {
					d[k] = d[i];
					d[i] = p;
					for (int j = 0; j < n; j++) {
						p = V[j][i];
						V[j][i] = V[j][k];
						V[j][k] = p;
					}
				}
			}
		}

		private void orthes() {
			int low = 0;
			int high = n - 1;

			for (int m = low + 1; m <= high - 1; m++) {
				double scale = 0.0;
				for (int i = m; i <= high; i++) {
					scale = scale + Math.abs(H[i][m - 1]);
				}
				if (scale != 0.0) {
					double h = 0.0;
					for (int i = high; i >= m; i--) {
						ort[i] = H[i][m - 1] / scale;
						h += ort[i] * ort[i];
					}
					double g = Math.sqrt(h);
					if (ort[m] > 0) {
						g = -g;
					}
					h = h - ort[m] * g;
					ort[m] = ort[m] - g;
					for (int j = m; j < n; j++) {
						double f = 0.0;
						for (int i = high; i >= m; i--) {
							f += ort[i] * H[i][j];
						}
						f = f / h;
						for (int i = m; i <= high; i++) {
							H[i][j] -= f * ort[i];
						}
					}

					for (int i = 0; i <= high; i++) {
						double f = 0.0;
						for (int j = high; j >= m; j--) {
							f += ort[j] * H[i][j];
						}
						f = f / h;
						for (int j = m; j <= high; j++) {
							H[i][j] -= f * ort[j];
						}
					}
					ort[m] = scale * ort[m];
					H[m][m - 1] = scale * g;
				}
			}
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					V[i][j] = (i == j ? 1.0 : 0.0);
				}
			}

			for (int m = high - 1; m >= low + 1; m--) {
				if (H[m][m - 1] != 0.0) {
					for (int i = m + 1; i <= high; i++) {
						ort[i] = H[i][m - 1];
					}
					for (int j = m; j <= high; j++) {
						double g = 0.0;
						for (int i = m; i <= high; i++) {
							g += ort[i] * V[i][j];
						}
						// Double division avoids possible underflow
						g = (g / ort[m]) / H[m][m - 1];
						for (int i = m; i <= high; i++) {
							V[i][j] += g * ort[i];
						}
					}
				}
			}
		}

		private void hqr2() {
			int nn = this.n;
			int n = nn - 1;
			int low = 0;
			int high = nn - 1;
			double eps = Math.pow(2.0, -52.0);
			double exshift = 0.0;
			double p = 0, q = 0, r = 0, s = 0, z = 0, t, w, x, y;

			// Store roots isolated by balanc and compute matrix norm
			double norm = 0.0;
			for (int i = 0; i < nn; i++) {
				if (i < low | i > high) {
					d[i] = H[i][i];
					e[i] = 0.0;
				}
				for (int j = Math.max(i - 1, 0); j < nn; j++) {
					norm = norm + Math.abs(H[i][j]);
				}
			}

			// Outer loop over eigenvalue index
			int iter = 0;
			while (n >= low) {
				// Look for single small sub-diagonal element
				int l = n;
				while (l > low) {
					s = Math.abs(H[l - 1][l - 1]) + Math.abs(H[l][l]);
					if (s == 0.0) {
						s = norm;
					}
					if (Math.abs(H[l][l - 1]) < eps * s) {
						break;
					}
					l--;
				}
				if (l == n) {
					H[n][n] = H[n][n] + exshift;
					d[n] = H[n][n];
					e[n] = 0.0;
					n--;
					iter = 0;
				} else if (l == n - 1) {
					w = H[n][n - 1] * H[n - 1][n];
					p = (H[n - 1][n - 1] - H[n][n]) / 2.0;
					q = p * p + w;
					z = Math.sqrt(Math.abs(q));
					H[n][n] = H[n][n] + exshift;
					H[n - 1][n - 1] = H[n - 1][n - 1] + exshift;
					x = H[n][n];

					// Real pair
					if (q >= 0) {
						if (p >= 0) {
							z = p + z;
						} else {
							z = p - z;
						}
						d[n - 1] = x + z;
						d[n] = d[n - 1];
						if (z != 0.0) {
							d[n] = x - w / z;
						}
						e[n - 1] = 0.0;
						e[n] = 0.0;
						x = H[n][n - 1];
						s = Math.abs(x) + Math.abs(z);
						p = x / s;
						q = z / s;
						r = Math.sqrt(p * p + q * q);
						p = p / r;
						q = q / r;

						// Row modification
						for (int j = n - 1; j < nn; j++) {
							z = H[n - 1][j];
							H[n - 1][j] = q * z + p * H[n][j];
							H[n][j] = q * H[n][j] - p * z;
						}

						// Column modification
						for (int i = 0; i <= n; i++) {
							z = H[i][n - 1];
							H[i][n - 1] = q * z + p * H[i][n];
							H[i][n] = q * H[i][n] - p * z;
						}

						// Accumulate transformations
						for (int i = low; i <= high; i++) {
							z = V[i][n - 1];
							V[i][n - 1] = q * z + p * V[i][n];
							V[i][n] = q * V[i][n] - p * z;
						}

						// Complex pair
					} else {
						d[n - 1] = x + p;
						d[n] = x + p;
						e[n - 1] = z;
						e[n] = -z;
					}
					n = n - 2;
					iter = 0;

					// No convergence yet
				} else {
					// Form shift
					x = H[n][n];
					y = 0.0;
					w = 0.0;
					if (l < n) {
						y = H[n - 1][n - 1];
						w = H[n][n - 1] * H[n - 1][n];
					}

					// Wilkinson's original ad hoc shift
					if (iter == 10) {
						exshift += x;
						for (int i = low; i <= n; i++) {
							H[i][i] -= x;
						}
						s = Math.abs(H[n][n - 1]) + Math.abs(H[n - 1][n - 2]);
						x = y = 0.75 * s;
						w = -0.4375 * s * s;
					}

					// MATLAB's new ad hoc shift
					if (iter == 30) {
						s = (y - x) / 2.0;
						s = s * s + w;
						if (s > 0) {
							s = Math.sqrt(s);
							if (y < x) {
								s = -s;
							}
							s = x - w / ((y - x) / 2.0 + s);
							for (int i = low; i <= n; i++) {
								H[i][i] -= s;
							}
							exshift += s;
							x = y = w = 0.964;
						}
					}
					iter = iter + 1; // (Could check iteration count here.)

					// Look for two consecutive small sub-diagonal elements
					int m = n - 2;
					while (m >= l) {
						z = H[m][m];
						r = x - z;
						s = y - z;
						p = (r * s - w) / H[m + 1][m] + H[m][m + 1];
						q = H[m + 1][m + 1] - z - r - s;
						r = H[m + 2][m + 1];
						s = Math.abs(p) + Math.abs(q) + Math.abs(r);
						p = p / s;
						q = q / s;
						r = r / s;
						if (m == l) {
							break;
						}
						if (Math.abs(H[m][m - 1]) * (Math.abs(q) + Math.abs(r)) < eps * (Math.abs(p)
								* (Math.abs(H[m - 1][m - 1]) + Math.abs(z) + Math.abs(H[m + 1][m + 1])))) {
							break;
						}
						m--;
					}

					for (int i = m + 2; i <= n; i++) {
						H[i][i - 2] = 0.0;
						if (i > m + 2) {
							H[i][i - 3] = 0.0;
						}
					}

					// Double QR step involving rows l:n and columns m:n
					for (int k = m; k <= n - 1; k++) {
						boolean notlast = (k != n - 1);
						if (k != m) {
							p = H[k][k - 1];
							q = H[k + 1][k - 1];
							r = (notlast ? H[k + 2][k - 1] : 0.0);
							x = Math.abs(p) + Math.abs(q) + Math.abs(r);
							if (x == 0.0) {
								continue;
							}
							p = p / x;
							q = q / x;
							r = r / x;
						}

						s = Math.sqrt(p * p + q * q + r * r);
						if (p < 0) {
							s = -s;
						}
						if (s != 0) {
							if (k != m) {
								H[k][k - 1] = -s * x;
							} else if (l != m) {
								H[k][k - 1] = -H[k][k - 1];
							}
							p = p + s;
							x = p / s;
							y = q / s;
							z = r / s;
							q = q / p;
							r = r / p;

							// Row modification
							for (int j = k; j < nn; j++) {
								p = H[k][j] + q * H[k + 1][j];
								if (notlast) {
									p = p + r * H[k + 2][j];
									H[k + 2][j] = H[k + 2][j] - p * z;
								}
								H[k][j] = H[k][j] - p * x;
								H[k + 1][j] = H[k + 1][j] - p * y;
							}

							// Column modification
							for (int i = 0; i <= Math.min(n, k + 3); i++) {
								p = x * H[i][k] + y * H[i][k + 1];
								if (notlast) {
									p = p + z * H[i][k + 2];
									H[i][k + 2] = H[i][k + 2] - p * r;
								}
								H[i][k] = H[i][k] - p;
								H[i][k + 1] = H[i][k + 1] - p * q;
							}

							// Accumulate transformations
							for (int i = low; i <= high; i++) {
								p = x * V[i][k] + y * V[i][k + 1];
								if (notlast) {
									p = p + z * V[i][k + 2];
									V[i][k + 2] = V[i][k + 2] - p * r;
								}
								V[i][k] = V[i][k] - p;
								V[i][k + 1] = V[i][k + 1] - p * q;
							}
						} // (s != 0)
					} // k loop
				} // check convergence
			} // while (n >= low)

			// Backsubstitute to find vectors of upper triangular form
			if (norm == 0.0) {
				return;
			}

			for (n = nn - 1; n >= 0; n--) {
				p = d[n];
				q = e[n];

				// Real vector
				if (q == 0) {
					int l = n;
					H[n][n] = 1.0;
					for (int i = n - 1; i >= 0; i--) {
						w = H[i][i] - p;
						r = 0.0;
						for (int j = l; j <= n; j++) {
							r = r + H[i][j] * H[j][n];
						}
						if (e[i] < 0.0) {
							z = w;
							s = r;
						} else {
							l = i;
							if (e[i] == 0.0) {
								if (w != 0.0) {
									H[i][n] = -r / w;
								} else {
									H[i][n] = -r / (eps * norm);
								}
								// Solve real equations
							} else {
								x = H[i][i + 1];
								y = H[i + 1][i];
								q = (d[i] - p) * (d[i] - p) + e[i] * e[i];
								t = (x * s - z * r) / q;
								H[i][n] = t;
								if (Math.abs(x) > Math.abs(z)) {
									H[i + 1][n] = (-r - w * t) / x;
								} else {
									H[i + 1][n] = (-s - y * t) / z;
								}
							}

							// Overflow control
							t = Math.abs(H[i][n]);
							if ((eps * t) * t > 1) {
								for (int j = i; j <= n; j++) {
									H[j][n] = H[j][n] / t;
								}
							}
						}
					}
					// Complex vector
				} else if (q < 0) {
					int l = n - 1;

					// Last vector component imaginary so matrix is triangular
					if (Math.abs(H[n][n - 1]) > Math.abs(H[n - 1][n])) {
						H[n - 1][n - 1] = q / H[n][n - 1];
						H[n - 1][n] = -(H[n][n] - p) / H[n][n - 1];
					} else {
						cdiv(0.0, -H[n - 1][n], H[n - 1][n - 1] - p, q);
						H[n - 1][n - 1] = cdivr;
						H[n - 1][n] = cdivi;
					}
					H[n][n - 1] = 0.0;
					H[n][n] = 1.0;
					for (int i = n - 2; i >= 0; i--) {
						double ra, sa, vr, vi;
						ra = 0.0;
						sa = 0.0;
						for (int j = l; j <= n; j++) {
							ra = ra + H[i][j] * H[j][n - 1];
							sa = sa + H[i][j] * H[j][n];
						}
						w = H[i][i] - p;

						if (e[i] < 0.0) {
							z = w;
							r = ra;
							s = sa;
						} else {
							l = i;
							if (e[i] == 0) {
								cdiv(-ra, -sa, w, q);
								H[i][n - 1] = cdivr;
								H[i][n] = cdivi;
							} else {

								// Solve complex equations
								x = H[i][i + 1];
								y = H[i + 1][i];
								vr = (d[i] - p) * (d[i] - p) + e[i] * e[i] - q * q;
								vi = (d[i] - p) * 2.0 * q;
								if (vr == 0.0 & vi == 0.0) {
									vr = eps * norm
											* (Math.abs(w) + Math.abs(q) + Math.abs(x) + Math.abs(y) + Math.abs(z));
								}
								cdiv(x * r - z * ra + q * sa, x * s - z * sa - q * ra, vr, vi);
								H[i][n - 1] = cdivr;
								H[i][n] = cdivi;
								if (Math.abs(x) > (Math.abs(z) + Math.abs(q))) {
									H[i + 1][n - 1] = (-ra - w * H[i][n - 1] + q * H[i][n]) / x;
									H[i + 1][n] = (-sa - w * H[i][n] - q * H[i][n - 1]) / x;
								} else {
									cdiv(-r - y * H[i][n - 1], -s - y * H[i][n], z, q);
									H[i + 1][n - 1] = cdivr;
									H[i + 1][n] = cdivi;
								}
							}

							// Overflow control
							t = Math.max(Math.abs(H[i][n - 1]), Math.abs(H[i][n]));
							if ((eps * t) * t > 1) {
								for (int j = i; j <= n; j++) {
									H[j][n - 1] = H[j][n - 1] / t;
									H[j][n] = H[j][n] / t;
								}
							}
						}
					}
				}
			}

			// Vectors of isolated roots
			for (int i = 0; i < nn; i++) {
				if (i < low | i > high) {
					for (int j = i; j < nn; j++) {
						V[i][j] = H[i][j];
					}
				}
			}

			// Back transformation to get eigenvectors of original matrix
			for (int j = nn - 1; j >= low; j--) {
				for (int i = low; i <= high; i++) {
					z = 0.0;
					for (int k = low; k <= Math.min(j, high); k++) {
						z = z + V[i][k] * H[k][j];
					}
					V[i][j] = z;
				}
			}
		}

		private void cdiv(double xr, double xi, double yr, double yi) {
			double r, d;
			if (Math.abs(yr) > Math.abs(yi)) {
				r = yi / yr;
				d = yr + r * yi;
				cdivr = (xr + r * xi) / d;
				cdivi = (xi - r * xr) / d;
			} else {
				r = yr / yi;
				d = yi + r * yr;
				cdivr = (r * xr + xi) / d;
				cdivi = (r * xi - xr) / d;
			}
		}

		public Eig(Matrix Arg) {
			assert (Arg.getRow() == Arg.getCol()) : "the format of the matrix is wrong!\n";
			double[][] A = Arg.getArray();
			n = Arg.getCol();
			V = new double[n][n];
			d = new double[n];
			e = new double[n];

			issymmetric = true;
			for (int j = 0; (j < n) & issymmetric; j++) {
				for (int i = 0; (i < n) & issymmetric; i++) {
					issymmetric = (A[i][j] == A[j][i]);
				}
			}

			if (issymmetric) {
				for (int i = 0; i < n; i++) {
					for (int j = 0; j < n; j++) {
						V[i][j] = A[i][j];
					}
				}

				// Tridiagonalize.
				tred2();

				// Diagonalize.
				tql2();
			} else {
				H = new double[n][n];
				ort = new double[n];

				for (int j = 0; j < n; j++) {
					for (int i = 0; i < n; i++) {
						H[i][j] = A[i][j];
					}
				}
				// Reduce to Hessenberg form.
				orthes();
				// Reduce Hessenberg to real Schur form.
				hqr2();
			}
		}

		public Matrix getD() {
			Matrix X = new Matrix(n, 1);
			double[][] D = X.getArray();
			for (int i = 0; i < n; i++)
				D[i][0] = d[i];
			return X;
		}

		public Matrix getV() {
			Matrix mat = new Matrix(V);
			Matrix m = new Matrix(mat.getRow(), mat.getCol());
			mat.copyInto(m);
			return m;
		}
	}

	public String toString() {
		StringBuffer str = new StringBuffer();
		for (int i = 0; i < getRow(); ++i) {
			for (int j = 0; j < getCol(); ++j) {
				str.append(elem[i][j] + " ");
			}
			str.append("\n");
		}
		return str.toString();
	}

	public static Matrix eye(int n) {
		assert (n > 0) : "argument must be positive integral.";
		Matrix e = new Matrix(n, n, 0);
		for (int i = 0; i < n; ++i)
			e.setElem(i, i, 1);
		return e;
	}

	private double elem[][] = null;
	private int row;
	private int col;
}