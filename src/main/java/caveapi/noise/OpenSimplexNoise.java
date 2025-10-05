package caveapi.noise;

public class OpenSimplexNoise
{
    private static final long PRIME_X = 5910200641878280303L;
    private static final long PRIME_Y = 6452764530575939509L;
    private static final long PRIME_Z = 6614699811220273867L;
    private static final long PRIME_W = 6254464313819354443L;
    private static final long HASH_MULTIPLIER = 6026932503003350773L;
    private static final long SEED_FLIP_3D = -5968755714895566377L;
    private static final double ROOT2OVER2 = 0.7071067811865476;
    private static final double SKEW_2D = 0.366025403784439;
    private static final double UNSKEW_2D = -0.21132486540518713;
    private static final double ROOT3OVER3 = 0.577350269189626;
    private static final double FALLBACK_ROTATE3 = 0.6666666666666666;
    private static final double ROTATE3_ORTHOGONALIZER = -0.21132486540518713;
    private static final float SKEW_4D = 0.309017f;
    private static final float UNSKEW_4D = -0.1381966f;
    private static final int N_GRADS_2D_EXPONENT = 7;
    private static final int N_GRADS_3D_EXPONENT = 8;
    private static final int N_GRADS_4D_EXPONENT = 9;
    private static final int N_GRADS_2D = 128;
    private static final int N_GRADS_3D = 256;
    private static final int N_GRADS_4D = 512;
    private static final double NORMALIZER_2D = 0.05481866495625118;
    private static final double NORMALIZER_3D = 0.2781926117527186;
    private static final double NORMALIZER_4D = 0.11127401889945551;
    private static final float RSQUARED_2D = 0.6666667f;
    private static final float RSQUARED_3D = 0.75f;
    private static final float RSQUARED_4D = 0.8f;
    private static float[] GRADIENTS_2D;
    private static float[] GRADIENTS_3D;
    private static float[] GRADIENTS_4D;
    private static int[] LOOKUP_4D_A;
    private static LatticeVertex4D[] LOOKUP_4D_B;

    public static float noise2(final long seed, final double x, final double y) {
        final double s = 0.366025403784439 * (x + y);
        final double xs = x + s;
        final double ys = y + s;
        return noise2_UnskewedBase(seed, xs, ys);
    }

    public static float noise2_ImproveX(final long seed, final double x, final double y) {
        final double xx = x * 0.7071067811865476;
        final double yy = y * 1.2247448713915896;
        return noise2_UnskewedBase(seed, yy + xx, yy - xx);
    }

    private static float noise2_UnskewedBase(final long seed, final double xs, final double ys) {
        final int xsb = fastFloor(xs);
        final int ysb = fastFloor(ys);
        final float xi = (float)(xs - xsb);
        final float yi = (float)(ys - ysb);
        final long xsbp = xsb * 5910200641878280303L;
        final long ysbp = ysb * 6452764530575939509L;
        final float t = (xi + yi) * -0.21132487f;
        final float dx0 = xi + t;
        final float dy0 = yi + t;
        final float a0 = 0.6666667f - dx0 * dx0 - dy0 * dy0;
        float value = a0 * a0 * (a0 * a0) * grad(seed, xsbp, ysbp, dx0, dy0);
        final float a2 = -3.1547005f * t + (-0.6666667f + a0);
        final float dx2 = dx0 - 0.57735026f;
        final float dy2 = dy0 - 0.57735026f;
        value += a2 * a2 * (a2 * a2) * grad(seed, xsbp + 5910200641878280303L, ysbp + 6452764530575939509L, dx2, dy2);
        final float xmyi = xi - yi;
        if (t < -0.21132486540518713) {
            if (xi + xmyi > 1.0f) {
                final float dx3 = dx0 - 1.3660254f;
                final float dy3 = dy0 - 0.36602542f;
                final float a3 = 0.6666667f - dx3 * dx3 - dy3 * dy3;
                if (a3 > 0.0f) {
                    value += a3 * a3 * (a3 * a3) * grad(seed, xsbp - 6626342789952991010L, ysbp + 6452764530575939509L, dx3, dy3);
                }
            }
            else {
                final float dx3 = dx0 + 0.21132487f;
                final float dy3 = dy0 - 0.7886751f;
                final float a3 = 0.6666667f - dx3 * dx3 - dy3 * dy3;
                if (a3 > 0.0f) {
                    value += a3 * a3 * (a3 * a3) * grad(seed, xsbp, ysbp + 6452764530575939509L, dx3, dy3);
                }
            }
            if (yi - xmyi > 1.0f) {
                final float dx4 = dx0 - 0.36602542f;
                final float dy4 = dy0 - 1.3660254f;
                final float a4 = 0.6666667f - dx4 * dx4 - dy4 * dy4;
                if (a4 > 0.0f) {
                    value += a4 * a4 * (a4 * a4) * grad(seed, xsbp + 5910200641878280303L, ysbp - 5541215012557672598L, dx4, dy4);
                }
            }
            else {
                final float dx4 = dx0 - 0.7886751f;
                final float dy4 = dy0 + 0.21132487f;
                final float a4 = 0.6666667f - dx4 * dx4 - dy4 * dy4;
                if (a4 > 0.0f) {
                    value += a4 * a4 * (a4 * a4) * grad(seed, xsbp + 5910200641878280303L, ysbp, dx4, dy4);
                }
            }
        }
        else {
            if (xi + xmyi < 0.0f) {
                final float dx3 = dx0 + 0.7886751f;
                final float dy3 = dy0 - 0.21132487f;
                final float a3 = 0.6666667f - dx3 * dx3 - dy3 * dy3;
                if (a3 > 0.0f) {
                    value += a3 * a3 * (a3 * a3) * grad(seed, xsbp - 5910200641878280303L, ysbp, dx3, dy3);
                }
            }
            else {
                final float dx3 = dx0 - 0.7886751f;
                final float dy3 = dy0 + 0.21132487f;
                final float a3 = 0.6666667f - dx3 * dx3 - dy3 * dy3;
                if (a3 > 0.0f) {
                    value += a3 * a3 * (a3 * a3) * grad(seed, xsbp + 5910200641878280303L, ysbp, dx3, dy3);
                }
            }
            if (yi < xmyi) {
                final float dx3 = dx0 - 0.21132487f;
                final float dy3 = dy0 + 0.7886751f;
                final float a3 = 0.6666667f - dx3 * dx3 - dy3 * dy3;
                if (a3 > 0.0f) {
                    value += a3 * a3 * (a3 * a3) * grad(seed, xsbp, ysbp - 6452764530575939509L, dx3, dy3);
                }
            }
            else {
                final float dx3 = dx0 + 0.21132487f;
                final float dy3 = dy0 - 0.7886751f;
                final float a3 = 0.6666667f - dx3 * dx3 - dy3 * dy3;
                if (a3 > 0.0f) {
                    value += a3 * a3 * (a3 * a3) * grad(seed, xsbp, ysbp + 6452764530575939509L, dx3, dy3);
                }
            }
        }
        return value;
    }

    public static float noise3_ImproveXY(final long seed, final double x, final double y, final double z) {
        final double xy = x + y;
        final double s2 = xy * -0.21132486540518713;
        final double zz = z * 0.577350269189626;
        final double xr = x + s2 + zz;
        final double yr = y + s2 + zz;
        final double zr = xy * -0.577350269189626 + zz;
        return noise3_UnrotatedBase(seed, xr, yr, zr);
    }

    public static float noise3_ImproveXZ(final long seed, final double x, final double y, final double z) {
        final double xz = x + z;
        final double s2 = xz * -0.211324865405187;
        final double yy = y * 0.577350269189626;
        final double xr = x + s2 + yy;
        final double zr = z + s2 + yy;
        final double yr = xz * -0.577350269189626 + yy;
        return noise3_UnrotatedBase(seed, xr, yr, zr);
    }

    public static float noise3_Fallback(final long seed, final double x, final double y, final double z) {
        final double r = 0.6666666666666666 * (x + y + z);
        final double xr = r - x;
        final double yr = r - y;
        final double zr = r - z;
        return noise3_UnrotatedBase(seed, xr, yr, zr);
    }

    private static float noise3_UnrotatedBase(final long seed, final double xr, final double yr, final double zr) {
        final int xrb = fastFloor(xr);
        final int yrb = fastFloor(yr);
        final int zrb = fastFloor(zr);
        final float xi = (float)(xr - xrb);
        final float yi = (float)(yr - yrb);
        final float zi = (float)(zr - zrb);
        final long xrbp = xrb * 5910200641878280303L;
        final long yrbp = yrb * 6452764530575939509L;
        final long zrbp = zrb * 6614699811220273867L;
        final long seed2 = seed ^ 0xAD2AB84D169129D7L;
        final int xNMask = (int)(-0.5f - xi);
        final int yNMask = (int)(-0.5f - yi);
        final int zNMask = (int)(-0.5f - zi);
        final float x0 = xi + xNMask;
        final float y0 = yi + yNMask;
        final float z0 = zi + zNMask;
        final float a0 = 0.75f - x0 * x0 - y0 * y0 - z0 * z0;
        float value = a0 * a0 * (a0 * a0) * grad(seed, xrbp + ((long)xNMask & 0x5205402B9270C86FL), yrbp + ((long)yNMask & 0x598CD327003817B5L), zrbp + ((long)zNMask & 0x5BCC226E9FA0BACBL), x0, y0, z0);
        final float x2 = xi - 0.5f;
        final float y2 = yi - 0.5f;
        final float z2 = zi - 0.5f;
        final float a2 = 0.75f - x2 * x2 - y2 * y2 - z2 * z2;
        value += a2 * a2 * (a2 * a2) * grad(seed2, xrbp + 5910200641878280303L, yrbp + 6452764530575939509L, zrbp + 6614699811220273867L, x2, y2, z2);
        final float xAFlipMask0 = ((xNMask | 0x1) << 1) * x2;
        final float yAFlipMask0 = ((yNMask | 0x1) << 1) * y2;
        final float zAFlipMask0 = ((zNMask | 0x1) << 1) * z2;
        final float xAFlipMask2 = (-2 - (xNMask << 2)) * x2 - 1.0f;
        final float yAFlipMask2 = (-2 - (yNMask << 2)) * y2 - 1.0f;
        final float zAFlipMask2 = (-2 - (zNMask << 2)) * z2 - 1.0f;
        boolean skip5 = false;
        final float a3 = xAFlipMask0 + a0;
        if (a3 > 0.0f) {
            final float x3 = x0 - (xNMask | 0x1);
            final float y3 = y0;
            final float z3 = z0;
            value += a3 * a3 * (a3 * a3) * grad(seed, xrbp + ((long)~xNMask & 0x5205402B9270C86FL), yrbp + ((long)yNMask & 0x598CD327003817B5L), zrbp + ((long)zNMask & 0x5BCC226E9FA0BACBL), x3, y3, z3);
        }
        else {
            final float a4 = yAFlipMask0 + zAFlipMask0 + a0;
            if (a4 > 0.0f) {
                final float x4 = x0;
                final float y4 = y0 - (yNMask | 0x1);
                final float z4 = z0 - (zNMask | 0x1);
                value += a4 * a4 * (a4 * a4) * grad(seed, xrbp + ((long)xNMask & 0x5205402B9270C86FL), yrbp + ((long)~yNMask & 0x598CD327003817B5L), zrbp + ((long)~zNMask & 0x5BCC226E9FA0BACBL), x4, y4, z4);
            }
            final float a5 = xAFlipMask2 + a2;
            if (a5 > 0.0f) {
                final float x5 = (xNMask | 0x1) + x2;
                final float y5 = y2;
                final float z5 = z2;
                value += a5 * a5 * (a5 * a5) * grad(seed2, xrbp + ((long)xNMask & 0xA40A805724E190DEL), yrbp + 6452764530575939509L, zrbp + 6614699811220273867L, x5, y5, z5);
                skip5 = true;
            }
        }
        boolean skip6 = false;
        final float a6 = yAFlipMask0 + a0;
        if (a6 > 0.0f) {
            final float x6 = x0;
            final float y6 = y0 - (yNMask | 0x1);
            final float z6 = z0;
            value += a6 * a6 * (a6 * a6) * grad(seed, xrbp + ((long)xNMask & 0x5205402B9270C86FL), yrbp + ((long)~yNMask & 0x598CD327003817B5L), zrbp + ((long)zNMask & 0x5BCC226E9FA0BACBL), x6, y6, z6);
        }
        else {
            final float a7 = xAFlipMask0 + zAFlipMask0 + a0;
            if (a7 > 0.0f) {
                final float x7 = x0 - (xNMask | 0x1);
                final float y7 = y0;
                final float z7 = z0 - (zNMask | 0x1);
                value += a7 * a7 * (a7 * a7) * grad(seed, xrbp + ((long)~xNMask & 0x5205402B9270C86FL), yrbp + ((long)yNMask & 0x598CD327003817B5L), zrbp + ((long)~zNMask & 0x5BCC226E9FA0BACBL), x7, y7, z7);
            }
            final float a8 = yAFlipMask2 + a2;
            if (a8 > 0.0f) {
                final float x8 = x2;
                final float y8 = (yNMask | 0x1) + y2;
                final float z8 = z2;
                value += a8 * a8 * (a8 * a8) * grad(seed2, xrbp + 5910200641878280303L, yrbp + ((long)yNMask & 0xB319A64E00702F6AL), zrbp + 6614699811220273867L, x8, y8, z8);
                skip6 = true;
            }
        }
        boolean skipD = false;
        final float aA = zAFlipMask0 + a0;
        if (aA > 0.0f) {
            final float xA = x0;
            final float yA = y0;
            final float zA = z0 - (zNMask | 0x1);
            value += aA * aA * (aA * aA) * grad(seed, xrbp + ((long)xNMask & 0x5205402B9270C86FL), yrbp + ((long)yNMask & 0x598CD327003817B5L), zrbp + ((long)~zNMask & 0x5BCC226E9FA0BACBL), xA, yA, zA);
        }
        else {
            final float aB = xAFlipMask0 + yAFlipMask0 + a0;
            if (aB > 0.0f) {
                final float xB = x0 - (xNMask | 0x1);
                final float yB = y0 - (yNMask | 0x1);
                final float zB = z0;
                value += aB * aB * (aB * aB) * grad(seed, xrbp + ((long)~xNMask & 0x5205402B9270C86FL), yrbp + ((long)~yNMask & 0x598CD327003817B5L), zrbp + ((long)zNMask & 0x5BCC226E9FA0BACBL), xB, yB, zB);
            }
            final float aC = zAFlipMask2 + a2;
            if (aC > 0.0f) {
                final float xC = x2;
                final float yC = y2;
                final float zC = (zNMask | 0x1) + z2;
                value += aC * aC * (aC * aC) * grad(seed2, xrbp + 5910200641878280303L, yrbp + 6452764530575939509L, zrbp + ((long)zNMask & 0xB79844DD3F417596L), xC, yC, zC);
                skipD = true;
            }
        }
        if (!skip5) {
            final float a9 = yAFlipMask2 + zAFlipMask2 + a2;
            if (a9 > 0.0f) {
                final float x9 = x2;
                final float y9 = (yNMask | 0x1) + y2;
                final float z9 = (zNMask | 0x1) + z2;
                value += a9 * a9 * (a9 * a9) * grad(seed2, xrbp + 5910200641878280303L, yrbp + ((long)yNMask & 0xB319A64E00702F6AL), zrbp + ((long)zNMask & 0xB79844DD3F417596L), x9, y9, z9);
            }
        }
        if (!skip6) {
            final float a10 = xAFlipMask2 + zAFlipMask2 + a2;
            if (a10 > 0.0f) {
                final float x10 = (xNMask | 0x1) + x2;
                final float y10 = y2;
                final float z10 = (zNMask | 0x1) + z2;
                value += a10 * a10 * (a10 * a10) * grad(seed2, xrbp + ((long)xNMask & 0xA40A805724E190DEL), yrbp + 6452764530575939509L, zrbp + ((long)zNMask & 0xB79844DD3F417596L), x10, y10, z10);
            }
        }
        if (!skipD) {
            final float aD = xAFlipMask2 + yAFlipMask2 + a2;
            if (aD > 0.0f) {
                final float xD = (xNMask | 0x1) + x2;
                final float yD = (yNMask | 0x1) + y2;
                final float zD = z2;
                value += aD * aD * (aD * aD) * grad(seed2, xrbp + ((long)xNMask & 0xA40A805724E190DEL), yrbp + ((long)yNMask & 0xB319A64E00702F6AL), zrbp + 6614699811220273867L, xD, yD, zD);
            }
        }
        return value;
    }

    public static float noise4_ImproveXYZ_ImproveXY(final long seed, final double x, final double y, final double z, final double w) {
        final double xy = x + y;
        final double s2 = xy * -0.211324865405187;
        final double zz = z * 0.2886751345948129;
        final double ww = w * 1.118033988749894;
        final double xr = x + (zz + ww + s2);
        final double yr = y + (zz + ww + s2);
        final double zr = xy * -0.577350269189626 + (zz + ww);
        final double wr = z * -0.866025403784439 + ww;
        return noise4_UnskewedBase(seed, xr, yr, zr, wr);
    }

    public static float noise4_ImproveXYZ_ImproveXZ(final long seed, final double x, final double y, final double z, final double w) {
        final double xz = x + z;
        final double s2 = xz * -0.211324865405187;
        final double yy = y * 0.2886751345948129;
        final double ww = w * 1.118033988749894;
        final double xr = x + (yy + ww + s2);
        final double zr = z + (yy + ww + s2);
        final double yr = xz * -0.577350269189626 + (yy + ww);
        final double wr = y * -0.866025403784439 + ww;
        return noise4_UnskewedBase(seed, xr, yr, zr, wr);
    }

    public static float noise4_ImproveXYZ(final long seed, final double x, final double y, final double z, final double w) {
        final double xyz = x + y + z;
        final double ww = w * 1.118033988749894;
        final double s2 = xyz * -0.16666666666666666 + ww;
        final double xs = x + s2;
        final double ys = y + s2;
        final double zs = z + s2;
        final double ws = -0.5 * xyz + ww;
        return noise4_UnskewedBase(seed, xs, ys, zs, ws);
    }

    public static float noise4_ImproveXY_ImproveZW(final long seed, final double x, final double y, final double z, final double w) {
        final double s2 = (x + y) * -0.2852251398743488 + (z + w) * 0.8389706547061143;
        final double t2 = (z + w) * 0.21939749883706436 + (x + y) * -0.48214856493302477;
        final double xs = x + s2;
        final double ys = y + s2;
        final double zs = z + t2;
        final double ws = w + t2;
        return noise4_UnskewedBase(seed, xs, ys, zs, ws);
    }

    public static float noise4_Fallback(final long seed, final double x, final double y, final double z, final double w) {
        final double s = 0.30901700258255005 * (x + y + z + w);
        final double xs = x + s;
        final double ys = y + s;
        final double zs = z + s;
        final double ws = w + s;
        return noise4_UnskewedBase(seed, xs, ys, zs, ws);
    }

    private static float noise4_UnskewedBase(final long seed, final double xs, final double ys, final double zs, final double ws) {
        final int xsb = fastFloor(xs);
        final int ysb = fastFloor(ys);
        final int zsb = fastFloor(zs);
        final int wsb = fastFloor(ws);
        final float xsi = (float)(xs - xsb);
        final float ysi = (float)(ys - ysb);
        final float zsi = (float)(zs - zsb);
        final float wsi = (float)(ws - wsb);
        final float ssi = (xsi + ysi + zsi + wsi) * -0.1381966f;
        final float xi = xsi + ssi;
        final float yi = ysi + ssi;
        final float zi = zsi + ssi;
        final float wi = wsi + ssi;
        final long xsvp = xsb * 5910200641878280303L;
        final long ysvp = ysb * 6452764530575939509L;
        final long zsvp = zsb * 6614699811220273867L;
        final long wsvp = wsb * 6254464313819354443L;
        final int index = (fastFloor(xs * 4.0) & 0x3) << 0 | (fastFloor(ys * 4.0) & 0x3) << 2 | (fastFloor(zs * 4.0) & 0x3) << 4 | (fastFloor(ws * 4.0) & 0x3) << 6;
        float value = 0.0f;
        final int secondaryIndexStartAndStop = OpenSimplexNoise.LOOKUP_4D_A[index];
        final int secondaryIndexStart = secondaryIndexStartAndStop & 0xFFFF;
        for (int secondaryIndexStop = secondaryIndexStartAndStop >> 16, i = secondaryIndexStart; i < secondaryIndexStop; ++i) {
            final LatticeVertex4D c = OpenSimplexNoise.LOOKUP_4D_B[i];
            final float dx = xi + c.dx;
            final float dy = yi + c.dy;
            final float dz = zi + c.dz;
            final float dw = wi + c.dw;
            float a = dx * dx + dy * dy + (dz * dz + dw * dw);
            if (a < 0.8f) {
                a -= 0.8f;
                a *= a;
                value += a * a * grad(seed, xsvp + c.xsvp, ysvp + c.ysvp, zsvp + c.zsvp, wsvp + c.wsvp, dx, dy, dz, dw);
            }
        }
        return value;
    }

    private static float grad(final long seed, final long xsvp, final long ysvp, final float dx, final float dy) {
        long hash = seed ^ xsvp ^ ysvp;
        hash *= 6026932503003350773L;
        hash ^= hash >> 58;
        final int gi = (int)hash & 0xFE;
        return OpenSimplexNoise.GRADIENTS_2D[gi | 0x0] * dx + OpenSimplexNoise.GRADIENTS_2D[gi | 0x1] * dy;
    }

    private static float grad(final long seed, final long xrvp, final long yrvp, final long zrvp, final float dx, final float dy, final float dz) {
        long hash = seed ^ xrvp ^ (yrvp ^ zrvp);
        hash *= 6026932503003350773L;
        hash ^= hash >> 58;
        final int gi = (int)hash & 0x3FC;
        return OpenSimplexNoise.GRADIENTS_3D[gi | 0x0] * dx + OpenSimplexNoise.GRADIENTS_3D[gi | 0x1] * dy + OpenSimplexNoise.GRADIENTS_3D[gi | 0x2] * dz;
    }

    private static float grad(final long seed, final long xsvp, final long ysvp, final long zsvp, final long wsvp, final float dx, final float dy, final float dz, final float dw) {
        long hash = seed ^ (xsvp ^ ysvp) ^ (zsvp ^ wsvp);
        hash *= 6026932503003350773L;
        hash ^= hash >> 57;
        final int gi = (int)hash & 0x7FC;
        return OpenSimplexNoise.GRADIENTS_4D[gi | 0x0] * dx + OpenSimplexNoise.GRADIENTS_4D[gi | 0x1] * dy + (OpenSimplexNoise.GRADIENTS_4D[gi | 0x2] * dz + OpenSimplexNoise.GRADIENTS_4D[gi | 0x3] * dw);
    }

    private static int fastFloor(final double x) {
        final int xi = (int)x;
        return (x < xi) ? (xi - 1) : xi;
    }

    static {
        OpenSimplexNoise.GRADIENTS_2D = new float[256];
        final float[] grad2 = { 0.38268343f, 0.9238795f, 0.9238795f, 0.38268343f, 0.9238795f, -0.38268343f, 0.38268343f, -0.9238795f, -0.38268343f, -0.9238795f, -0.9238795f, -0.38268343f, -0.9238795f, 0.38268343f, -0.38268343f, 0.9238795f, 0.13052619f, 0.9914449f, 0.6087614f, 0.7933533f, 0.7933533f, 0.6087614f, 0.9914449f, 0.13052619f, 0.9914449f, -0.13052619f, 0.7933533f, -0.6087614f, 0.6087614f, -0.7933533f, 0.13052619f, -0.9914449f, -0.13052619f, -0.9914449f, -0.6087614f, -0.7933533f, -0.7933533f, -0.6087614f, -0.9914449f, -0.13052619f, -0.9914449f, 0.13052619f, -0.7933533f, 0.6087614f, -0.6087614f, 0.7933533f, -0.13052619f, 0.9914449f };
        for (int i = 0; i < grad2.length; ++i) {
            grad2[i] /= (float)0.05481866495625118;
        }
        for (int i = 0, j = 0; i < OpenSimplexNoise.GRADIENTS_2D.length; ++i, ++j) {
            if (j == grad2.length) {
                j = 0;
            }
            OpenSimplexNoise.GRADIENTS_2D[i] = grad2[j];
        }
        OpenSimplexNoise.GRADIENTS_3D = new float[1024];
        final float[] grad3 = { 2.2247448f, 2.2247448f, -1.0f, 0.0f, 2.2247448f, 2.2247448f, 1.0f, 0.0f, 3.0862665f, 1.1721513f, 0.0f, 0.0f, 1.1721513f, 3.0862665f, 0.0f, 0.0f, -2.2247448f, 2.2247448f, -1.0f, 0.0f, -2.2247448f, 2.2247448f, 1.0f, 0.0f, -1.1721513f, 3.0862665f, 0.0f, 0.0f, -3.0862665f, 1.1721513f, 0.0f, 0.0f, -1.0f, -2.2247448f, -2.2247448f, 0.0f, 1.0f, -2.2247448f, -2.2247448f, 0.0f, 0.0f, -3.0862665f, -1.1721513f, 0.0f, 0.0f, -1.1721513f, -3.0862665f, 0.0f, -1.0f, -2.2247448f, 2.2247448f, 0.0f, 1.0f, -2.2247448f, 2.2247448f, 0.0f, 0.0f, -1.1721513f, 3.0862665f, 0.0f, 0.0f, -3.0862665f, 1.1721513f, 0.0f, -2.2247448f, -2.2247448f, -1.0f, 0.0f, -2.2247448f, -2.2247448f, 1.0f, 0.0f, -3.0862665f, -1.1721513f, 0.0f, 0.0f, -1.1721513f, -3.0862665f, 0.0f, 0.0f, -2.2247448f, -1.0f, -2.2247448f, 0.0f, -2.2247448f, 1.0f, -2.2247448f, 0.0f, -1.1721513f, 0.0f, -3.0862665f, 0.0f, -3.0862665f, 0.0f, -1.1721513f, 0.0f, -2.2247448f, -1.0f, 2.2247448f, 0.0f, -2.2247448f, 1.0f, 2.2247448f, 0.0f, -3.0862665f, 0.0f, 1.1721513f, 0.0f, -1.1721513f, 0.0f, 3.0862665f, 0.0f, -1.0f, 2.2247448f, -2.2247448f, 0.0f, 1.0f, 2.2247448f, -2.2247448f, 0.0f, 0.0f, 1.1721513f, -3.0862665f, 0.0f, 0.0f, 3.0862665f, -1.1721513f, 0.0f, -1.0f, 2.2247448f, 2.2247448f, 0.0f, 1.0f, 2.2247448f, 2.2247448f, 0.0f, 0.0f, 3.0862665f, 1.1721513f, 0.0f, 0.0f, 1.1721513f, 3.0862665f, 0.0f, 2.2247448f, -2.2247448f, -1.0f, 0.0f, 2.2247448f, -2.2247448f, 1.0f, 0.0f, 1.1721513f, -3.0862665f, 0.0f, 0.0f, 3.0862665f, -1.1721513f, 0.0f, 0.0f, 2.2247448f, -1.0f, -2.2247448f, 0.0f, 2.2247448f, 1.0f, -2.2247448f, 0.0f, 3.0862665f, 0.0f, -1.1721513f, 0.0f, 1.1721513f, 0.0f, -3.0862665f, 0.0f, 2.2247448f, -1.0f, 2.2247448f, 0.0f, 2.2247448f, 1.0f, 2.2247448f, 0.0f, 1.1721513f, 0.0f, 3.0862665f, 0.0f, 3.0862665f, 0.0f, 1.1721513f, 0.0f };
        for (int k = 0; k < grad3.length; ++k) {
            grad3[k] /= (float)0.2781926117527186;
        }
        for (int k = 0, l = 0; k < OpenSimplexNoise.GRADIENTS_3D.length; ++k, ++l) {
            if (l == grad3.length) {
                l = 0;
            }
            OpenSimplexNoise.GRADIENTS_3D[k] = grad3[l];
        }
        OpenSimplexNoise.GRADIENTS_4D = new float[2048];
        final float[] grad4 = { -0.6740059f, -0.32398477f, -0.32398477f, 0.5794685f, -0.7504884f, -0.40046722f, 0.15296486f, 0.502986f, -0.7504884f, 0.15296486f, -0.40046722f, 0.502986f, -0.8828162f, 0.08164729f, 0.08164729f, 0.4553054f, -0.4553054f, -0.08164729f, -0.08164729f, 0.8828162f, -0.502986f, -0.15296486f, 0.40046722f, 0.7504884f, -0.502986f, 0.40046722f, -0.15296486f, 0.7504884f, -0.5794685f, 0.32398477f, 0.32398477f, 0.6740059f, -0.6740059f, -0.32398477f, 0.5794685f, -0.32398477f, -0.7504884f, -0.40046722f, 0.502986f, 0.15296486f, -0.7504884f, 0.15296486f, 0.502986f, -0.40046722f, -0.8828162f, 0.08164729f, 0.4553054f, 0.08164729f, -0.4553054f, -0.08164729f, 0.8828162f, -0.08164729f, -0.502986f, -0.15296486f, 0.7504884f, 0.40046722f, -0.502986f, 0.40046722f, 0.7504884f, -0.15296486f, -0.5794685f, 0.32398477f, 0.6740059f, 0.32398477f, -0.6740059f, 0.5794685f, -0.32398477f, -0.32398477f, -0.7504884f, 0.502986f, -0.40046722f, 0.15296486f, -0.7504884f, 0.502986f, 0.15296486f, -0.40046722f, -0.8828162f, 0.4553054f, 0.08164729f, 0.08164729f, -0.4553054f, 0.8828162f, -0.08164729f, -0.08164729f, -0.502986f, 0.7504884f, -0.15296486f, 0.40046722f, -0.502986f, 0.7504884f, 0.40046722f, -0.15296486f, -0.5794685f, 0.6740059f, 0.32398477f, 0.32398477f, 0.5794685f, -0.6740059f, -0.32398477f, -0.32398477f, 0.502986f, -0.7504884f, -0.40046722f, 0.15296486f, 0.502986f, -0.7504884f, 0.15296486f, -0.40046722f, 0.4553054f, -0.8828162f, 0.08164729f, 0.08164729f, 0.8828162f, -0.4553054f, -0.08164729f, -0.08164729f, 0.7504884f, -0.502986f, -0.15296486f, 0.40046722f, 0.7504884f, -0.502986f, 0.40046722f, -0.15296486f, 0.6740059f, -0.5794685f, 0.32398477f, 0.32398477f, -0.753341f, -0.3796829f, -0.3796829f, -0.3796829f, -0.78216845f, -0.43214726f, -0.43214726f, 0.121284805f, -0.78216845f, -0.43214726f, 0.121284805f, -0.43214726f, -0.78216845f, 0.121284805f, -0.43214726f, -0.43214726f, -0.85865086f, -0.5086297f, 0.04480237f, 0.04480237f, -0.85865086f, 0.04480237f, -0.5086297f, 0.04480237f, -0.85865086f, 0.04480237f, 0.04480237f, -0.5086297f, -0.9982829f, -0.033819415f, -0.033819415f, -0.033819415f, -0.3796829f, -0.753341f, -0.3796829f, -0.3796829f, -0.43214726f, -0.78216845f, -0.43214726f, 0.121284805f, -0.43214726f, -0.78216845f, 0.121284805f, -0.43214726f, 0.121284805f, -0.78216845f, -0.43214726f, -0.43214726f, -0.5086297f, -0.85865086f, 0.04480237f, 0.04480237f, 0.04480237f, -0.85865086f, -0.5086297f, 0.04480237f, 0.04480237f, -0.85865086f, 0.04480237f, -0.5086297f, -0.033819415f, -0.9982829f, -0.033819415f, -0.033819415f, -0.3796829f, -0.3796829f, -0.753341f, -0.3796829f, -0.43214726f, -0.43214726f, -0.78216845f, 0.121284805f, -0.43214726f, 0.121284805f, -0.78216845f, -0.43214726f, 0.121284805f, -0.43214726f, -0.78216845f, -0.43214726f, -0.5086297f, 0.04480237f, -0.85865086f, 0.04480237f, 0.04480237f, -0.5086297f, -0.85865086f, 0.04480237f, 0.04480237f, 0.04480237f, -0.85865086f, -0.5086297f, -0.033819415f, -0.033819415f, -0.9982829f, -0.033819415f, -0.3796829f, -0.3796829f, -0.3796829f, -0.753341f, -0.43214726f, -0.43214726f, 0.121284805f, -0.78216845f, -0.43214726f, 0.121284805f, -0.43214726f, -0.78216845f, 0.121284805f, -0.43214726f, -0.43214726f, -0.78216845f, -0.5086297f, 0.04480237f, 0.04480237f, -0.85865086f, 0.04480237f, -0.5086297f, 0.04480237f, -0.85865086f, 0.04480237f, 0.04480237f, -0.5086297f, -0.85865086f, -0.033819415f, -0.033819415f, -0.033819415f, -0.9982829f, -0.32398477f, -0.6740059f, -0.32398477f, 0.5794685f, -0.40046722f, -0.7504884f, 0.15296486f, 0.502986f, 0.15296486f, -0.7504884f, -0.40046722f, 0.502986f, 0.08164729f, -0.8828162f, 0.08164729f, 0.4553054f, -0.08164729f, -0.4553054f, -0.08164729f, 0.8828162f, -0.15296486f, -0.502986f, 0.40046722f, 0.7504884f, 0.40046722f, -0.502986f, -0.15296486f, 0.7504884f, 0.32398477f, -0.5794685f, 0.32398477f, 0.6740059f, -0.32398477f, -0.32398477f, -0.6740059f, 0.5794685f, -0.40046722f, 0.15296486f, -0.7504884f, 0.502986f, 0.15296486f, -0.40046722f, -0.7504884f, 0.502986f, 0.08164729f, 0.08164729f, -0.8828162f, 0.4553054f, -0.08164729f, -0.08164729f, -0.4553054f, 0.8828162f, -0.15296486f, 0.40046722f, -0.502986f, 0.7504884f, 0.40046722f, -0.15296486f, -0.502986f, 0.7504884f, 0.32398477f, 0.32398477f, -0.5794685f, 0.6740059f, -0.32398477f, -0.6740059f, 0.5794685f, -0.32398477f, -0.40046722f, -0.7504884f, 0.502986f, 0.15296486f, 0.15296486f, -0.7504884f, 0.502986f, -0.40046722f, 0.08164729f, -0.8828162f, 0.4553054f, 0.08164729f, -0.08164729f, -0.4553054f, 0.8828162f, -0.08164729f, -0.15296486f, -0.502986f, 0.7504884f, 0.40046722f, 0.40046722f, -0.502986f, 0.7504884f, -0.15296486f, 0.32398477f, -0.5794685f, 0.6740059f, 0.32398477f, -0.32398477f, -0.32398477f, 0.5794685f, -0.6740059f, -0.40046722f, 0.15296486f, 0.502986f, -0.7504884f, 0.15296486f, -0.40046722f, 0.502986f, -0.7504884f, 0.08164729f, 0.08164729f, 0.4553054f, -0.8828162f, -0.08164729f, -0.08164729f, 0.8828162f, -0.4553054f, -0.15296486f, 0.40046722f, 0.7504884f, -0.502986f, 0.40046722f, -0.15296486f, 0.7504884f, -0.502986f, 0.32398477f, 0.32398477f, 0.6740059f, -0.5794685f, -0.32398477f, 0.5794685f, -0.6740059f, -0.32398477f, -0.40046722f, 0.502986f, -0.7504884f, 0.15296486f, 0.15296486f, 0.502986f, -0.7504884f, -0.40046722f, 0.08164729f, 0.4553054f, -0.8828162f, 0.08164729f, -0.08164729f, 0.8828162f, -0.4553054f, -0.08164729f, -0.15296486f, 0.7504884f, -0.502986f, 0.40046722f, 0.40046722f, 0.7504884f, -0.502986f, -0.15296486f, 0.32398477f, 0.6740059f, -0.5794685f, 0.32398477f, -0.32398477f, 0.5794685f, -0.32398477f, -0.6740059f, -0.40046722f, 0.502986f, 0.15296486f, -0.7504884f, 0.15296486f, 0.502986f, -0.40046722f, -0.7504884f, 0.08164729f, 0.4553054f, 0.08164729f, -0.8828162f, -0.08164729f, 0.8828162f, -0.08164729f, -0.4553054f, -0.15296486f, 0.7504884f, 0.40046722f, -0.502986f, 0.40046722f, 0.7504884f, -0.15296486f, -0.502986f, 0.32398477f, 0.6740059f, 0.32398477f, -0.5794685f, 0.5794685f, -0.32398477f, -0.6740059f, -0.32398477f, 0.502986f, -0.40046722f, -0.7504884f, 0.15296486f, 0.502986f, 0.15296486f, -0.7504884f, -0.40046722f, 0.4553054f, 0.08164729f, -0.8828162f, 0.08164729f, 0.8828162f, -0.08164729f, -0.4553054f, -0.08164729f, 0.7504884f, -0.15296486f, -0.502986f, 0.40046722f, 0.7504884f, 0.40046722f, -0.502986f, -0.15296486f, 0.6740059f, 0.32398477f, -0.5794685f, 0.32398477f, 0.5794685f, -0.32398477f, -0.32398477f, -0.6740059f, 0.502986f, -0.40046722f, 0.15296486f, -0.7504884f, 0.502986f, 0.15296486f, -0.40046722f, -0.7504884f, 0.4553054f, 0.08164729f, 0.08164729f, -0.8828162f, 0.8828162f, -0.08164729f, -0.08164729f, -0.4553054f, 0.7504884f, -0.15296486f, 0.40046722f, -0.502986f, 0.7504884f, 0.40046722f, -0.15296486f, -0.502986f, 0.6740059f, 0.32398477f, 0.32398477f, -0.5794685f, 0.033819415f, 0.033819415f, 0.033819415f, 0.9982829f, -0.04480237f, -0.04480237f, 0.5086297f, 0.85865086f, -0.04480237f, 0.5086297f, -0.04480237f, 0.85865086f, -0.121284805f, 0.43214726f, 0.43214726f, 0.78216845f, 0.5086297f, -0.04480237f, -0.04480237f, 0.85865086f, 0.43214726f, -0.121284805f, 0.43214726f, 0.78216845f, 0.43214726f, 0.43214726f, -0.121284805f, 0.78216845f, 0.3796829f, 0.3796829f, 0.3796829f, 0.753341f, 0.033819415f, 0.033819415f, 0.9982829f, 0.033819415f, -0.04480237f, 0.04480237f, 0.85865086f, 0.5086297f, -0.04480237f, 0.5086297f, 0.85865086f, -0.04480237f, -0.121284805f, 0.43214726f, 0.78216845f, 0.43214726f, 0.5086297f, -0.04480237f, 0.85865086f, -0.04480237f, 0.43214726f, -0.121284805f, 0.78216845f, 0.43214726f, 0.43214726f, 0.43214726f, 0.78216845f, -0.121284805f, 0.3796829f, 0.3796829f, 0.753341f, 0.3796829f, 0.033819415f, 0.9982829f, 0.033819415f, 0.033819415f, -0.04480237f, 0.85865086f, -0.04480237f, 0.5086297f, -0.04480237f, 0.85865086f, 0.5086297f, -0.04480237f, -0.121284805f, 0.78216845f, 0.43214726f, 0.43214726f, 0.5086297f, 0.85865086f, -0.04480237f, -0.04480237f, 0.43214726f, 0.78216845f, -0.121284805f, 0.43214726f, 0.43214726f, 0.78216845f, 0.43214726f, -0.121284805f, 0.3796829f, 0.753341f, 0.3796829f, 0.3796829f, 0.9982829f, 0.033819415f, 0.033819415f, 0.033819415f, 0.85865086f, -0.04480237f, -0.04480237f, 0.5086297f, 0.85865086f, -0.04480237f, 0.5086297f, -0.04480237f, 0.78216845f, -0.121284805f, 0.43214726f, 0.43214726f, 0.85865086f, 0.5086297f, -0.04480237f, -0.04480237f, 0.78216845f, 0.43214726f, -0.121284805f, 0.43214726f, 0.78216845f, 0.43214726f, 0.43214726f, -0.121284805f, 0.753341f, 0.3796829f, 0.3796829f, 0.3796829f };
        for (int m = 0; m < grad4.length; ++m) {
            grad4[m] /= (float)0.11127401889945551;
        }
        for (int m = 0, j2 = 0; m < OpenSimplexNoise.GRADIENTS_4D.length; ++m, ++j2) {
            if (j2 == grad4.length) {
                j2 = 0;
            }
            OpenSimplexNoise.GRADIENTS_4D[m] = grad4[j2];
        }
        final int[][] lookup4DVertexCodes = { { 21, 69, 81, 84, 85, 86, 89, 90, 101, 102, 105, 106, 149, 150, 153, 154, 165, 166, 169, 170 }, { 21, 69, 81, 85, 86, 89, 90, 101, 102, 106, 149, 150, 154, 166, 170 }, { 1, 5, 17, 21, 65, 69, 81, 85, 86, 90, 102, 106, 150, 154, 166, 170 }, { 1, 21, 22, 69, 70, 81, 82, 85, 86, 90, 102, 106, 150, 154, 166, 170, 171 }, { 21, 69, 84, 85, 86, 89, 90, 101, 105, 106, 149, 153, 154, 169, 170 }, { 5, 21, 69, 85, 86, 89, 90, 101, 102, 105, 106, 149, 150, 153, 154, 170 }, { 5, 21, 69, 85, 86, 89, 90, 102, 106, 150, 154, 170 }, { 5, 21, 22, 69, 70, 85, 86, 89, 90, 102, 106, 150, 154, 170, 171 }, { 4, 5, 20, 21, 68, 69, 84, 85, 89, 90, 105, 106, 153, 154, 169, 170 }, { 5, 21, 69, 85, 86, 89, 90, 105, 106, 153, 154, 170 }, { 5, 21, 69, 85, 86, 89, 90, 106, 154, 170 }, { 5, 21, 22, 69, 70, 85, 86, 89, 90, 91, 106, 154, 170, 171 }, { 4, 21, 25, 69, 73, 84, 85, 88, 89, 90, 105, 106, 153, 154, 169, 170, 174 }, { 5, 21, 25, 69, 73, 85, 86, 89, 90, 105, 106, 153, 154, 170, 174 }, { 5, 21, 25, 69, 73, 85, 86, 89, 90, 94, 106, 154, 170, 174 }, { 5, 21, 26, 69, 74, 85, 86, 89, 90, 91, 94, 106, 154, 170, 171, 174, 175 }, { 21, 81, 84, 85, 86, 89, 101, 102, 105, 106, 149, 165, 166, 169, 170 }, { 17, 21, 81, 85, 86, 89, 90, 101, 102, 105, 106, 149, 150, 165, 166, 170 }, { 17, 21, 81, 85, 86, 90, 101, 102, 106, 150, 166, 170 }, { 17, 21, 22, 81, 82, 85, 86, 90, 101, 102, 106, 150, 166, 170, 171 }, { 20, 21, 84, 85, 86, 89, 90, 101, 102, 105, 106, 149, 153, 165, 169, 170 }, { 21, 85, 86, 89, 90, 101, 102, 105, 106, 149, 154, 166, 169, 170 }, { 21, 85, 86, 89, 90, 101, 102, 105, 106, 150, 154, 166, 170, 171 }, { 21, 22, 85, 86, 90, 102, 106, 107, 150, 154, 166, 170, 171 }, { 20, 21, 84, 85, 89, 90, 101, 105, 106, 153, 169, 170 }, { 21, 85, 86, 89, 90, 101, 102, 105, 106, 153, 154, 169, 170, 174 }, { 21, 85, 86, 89, 90, 101, 102, 105, 106, 154, 170 }, { 21, 22, 85, 86, 89, 90, 102, 106, 107, 154, 170, 171 }, { 20, 21, 25, 84, 85, 88, 89, 90, 101, 105, 106, 153, 169, 170, 174 }, { 21, 25, 85, 89, 90, 105, 106, 110, 153, 154, 169, 170, 174 }, { 21, 25, 85, 86, 89, 90, 105, 106, 110, 154, 170, 174 }, { 21, 26, 85, 86, 89, 90, 106, 107, 110, 154, 170, 171, 174, 175 }, { 16, 17, 20, 21, 80, 81, 84, 85, 101, 102, 105, 106, 165, 166, 169, 170 }, { 17, 21, 81, 85, 86, 101, 102, 105, 106, 165, 166, 170 }, { 17, 21, 81, 85, 86, 101, 102, 106, 166, 170 }, { 17, 21, 22, 81, 82, 85, 86, 101, 102, 103, 106, 166, 170, 171 }, { 20, 21, 84, 85, 89, 101, 102, 105, 106, 165, 169, 170 }, { 21, 85, 86, 89, 90, 101, 102, 105, 106, 165, 166, 169, 170, 186 }, { 21, 85, 86, 89, 90, 101, 102, 105, 106, 166, 170 }, { 21, 22, 85, 86, 90, 101, 102, 106, 107, 166, 170, 171 }, { 20, 21, 84, 85, 89, 101, 105, 106, 169, 170 }, { 21, 85, 86, 89, 90, 101, 102, 105, 106, 169, 170 }, { 21, 85, 86, 89, 90, 101, 102, 105, 106, 170 }, { 21, 22, 85, 86, 89, 90, 101, 102, 105, 106, 107, 170, 171 }, { 20, 21, 25, 84, 85, 88, 89, 101, 105, 106, 109, 169, 170, 174 }, { 21, 25, 85, 89, 90, 101, 105, 106, 110, 169, 170, 174 }, { 21, 25, 85, 86, 89, 90, 101, 102, 105, 106, 110, 170, 174 }, { 21, 85, 86, 89, 90, 102, 105, 106, 107, 110, 154, 170, 171, 174, 175 }, { 16, 21, 37, 81, 84, 85, 97, 100, 101, 102, 105, 106, 165, 166, 169, 170, 186 }, { 17, 21, 37, 81, 85, 86, 97, 101, 102, 105, 106, 165, 166, 170, 186 }, { 17, 21, 37, 81, 85, 86, 97, 101, 102, 106, 118, 166, 170, 186 }, { 17, 21, 38, 81, 85, 86, 98, 101, 102, 103, 106, 118, 166, 170, 171, 186, 187 }, { 20, 21, 37, 84, 85, 89, 100, 101, 102, 105, 106, 165, 169, 170, 186 }, { 21, 37, 85, 101, 102, 105, 106, 122, 165, 166, 169, 170, 186 }, { 21, 37, 85, 86, 101, 102, 105, 106, 122, 166, 170, 186 }, { 21, 38, 85, 86, 101, 102, 106, 107, 122, 166, 170, 171, 186, 187 }, { 20, 21, 37, 84, 85, 89, 100, 101, 105, 106, 121, 169, 170, 186 }, { 21, 37, 85, 89, 101, 102, 105, 106, 122, 169, 170, 186 }, { 21, 37, 85, 86, 89, 90, 101, 102, 105, 106, 122, 170, 186 }, { 21, 85, 86, 90, 101, 102, 105, 106, 107, 122, 166, 170, 171, 186, 187 }, { 20, 21, 41, 84, 85, 89, 101, 104, 105, 106, 109, 121, 169, 170, 174, 186, 190 }, { 21, 41, 85, 89, 101, 105, 106, 110, 122, 169, 170, 174, 186, 190 }, { 21, 85, 89, 90, 101, 102, 105, 106, 110, 122, 169, 170, 174, 186, 190 }, { 21, 85, 86, 89, 90, 101, 102, 105, 106, 107, 110, 122, 170, 171, 174, 186, 191 }, { 69, 81, 84, 85, 86, 89, 101, 149, 150, 153, 154, 165, 166, 169, 170 }, { 65, 69, 81, 85, 86, 89, 90, 101, 102, 149, 150, 153, 154, 165, 166, 170 }, { 65, 69, 81, 85, 86, 90, 102, 149, 150, 154, 166, 170 }, { 65, 69, 70, 81, 82, 85, 86, 90, 102, 149, 150, 154, 166, 170, 171 }, { 68, 69, 84, 85, 86, 89, 90, 101, 105, 149, 150, 153, 154, 165, 169, 170 }, { 69, 85, 86, 89, 90, 101, 106, 149, 150, 153, 154, 166, 169, 170 }, { 69, 85, 86, 89, 90, 102, 106, 149, 150, 153, 154, 166, 170, 171 }, { 69, 70, 85, 86, 90, 102, 106, 150, 154, 155, 166, 170, 171 }, { 68, 69, 84, 85, 89, 90, 105, 149, 153, 154, 169, 170 }, { 69, 85, 86, 89, 90, 105, 106, 149, 150, 153, 154, 169, 170, 174 }, { 69, 85, 86, 89, 90, 106, 149, 150, 153, 154, 170 }, { 69, 70, 85, 86, 89, 90, 106, 150, 154, 155, 170, 171 }, { 68, 69, 73, 84, 85, 88, 89, 90, 105, 149, 153, 154, 169, 170, 174 }, { 69, 73, 85, 89, 90, 105, 106, 153, 154, 158, 169, 170, 174 }, { 69, 73, 85, 86, 89, 90, 106, 153, 154, 158, 170, 174 }, { 69, 74, 85, 86, 89, 90, 106, 154, 155, 158, 170, 171, 174, 175 }, { 80, 81, 84, 85, 86, 89, 101, 102, 105, 149, 150, 153, 165, 166, 169, 170 }, { 81, 85, 86, 89, 101, 102, 106, 149, 150, 154, 165, 166, 169, 170 }, { 81, 85, 86, 90, 101, 102, 106, 149, 150, 154, 165, 166, 170, 171 }, { 81, 82, 85, 86, 90, 102, 106, 150, 154, 166, 167, 170, 171 }, { 84, 85, 86, 89, 101, 105, 106, 149, 153, 154, 165, 166, 169, 170 }, { 85, 86, 89, 90, 101, 102, 105, 106, 149, 150, 153, 154, 165, 166, 169, 170 }, { 21, 69, 81, 85, 86, 89, 90, 101, 102, 106, 149, 150, 154, 166, 170, 171 }, { 85, 86, 90, 102, 106, 150, 154, 166, 170, 171 }, { 84, 85, 89, 90, 101, 105, 106, 149, 153, 154, 165, 169, 170, 174 }, { 21, 69, 84, 85, 86, 89, 90, 101, 105, 106, 149, 153, 154, 169, 170, 174 }, { 21, 69, 85, 86, 89, 90, 101, 102, 105, 106, 149, 150, 153, 154, 166, 169, 170, 171, 174 }, { 85, 86, 89, 90, 102, 106, 150, 154, 166, 170, 171 }, { 84, 85, 88, 89, 90, 105, 106, 153, 154, 169, 170, 173, 174 }, { 85, 89, 90, 105, 106, 153, 154, 169, 170, 174 }, { 85, 86, 89, 90, 105, 106, 153, 154, 169, 170, 174 }, { 85, 86, 89, 90, 106, 154, 170, 171, 174, 175 }, { 80, 81, 84, 85, 101, 102, 105, 149, 165, 166, 169, 170 }, { 81, 85, 86, 101, 102, 105, 106, 149, 150, 165, 166, 169, 170, 186 }, { 81, 85, 86, 101, 102, 106, 149, 150, 165, 166, 170 }, { 81, 82, 85, 86, 101, 102, 106, 150, 166, 167, 170, 171 }, { 84, 85, 89, 101, 102, 105, 106, 149, 153, 165, 166, 169, 170, 186 }, { 21, 81, 84, 85, 86, 89, 101, 102, 105, 106, 149, 165, 166, 169, 170, 186 }, { 21, 81, 85, 86, 89, 90, 101, 102, 105, 106, 149, 150, 154, 165, 166, 169, 170, 171, 186 }, { 85, 86, 90, 101, 102, 106, 150, 154, 166, 170, 171 }, { 84, 85, 89, 101, 105, 106, 149, 153, 165, 169, 170 }, { 21, 84, 85, 86, 89, 90, 101, 102, 105, 106, 149, 153, 154, 165, 166, 169, 170, 174, 186 }, { 21, 85, 86, 89, 90, 101, 102, 105, 106, 154, 166, 169, 170 }, { 21, 85, 86, 89, 90, 101, 102, 105, 106, 150, 154, 166, 170, 171 }, { 84, 85, 88, 89, 101, 105, 106, 153, 169, 170, 173, 174 }, { 85, 89, 90, 101, 105, 106, 153, 154, 169, 170, 174 }, { 21, 85, 86, 89, 90, 101, 102, 105, 106, 153, 154, 169, 170, 174 }, { 21, 85, 86, 89, 90, 102, 105, 106, 154, 170, 171, 174, 175 }, { 80, 81, 84, 85, 97, 100, 101, 102, 105, 149, 165, 166, 169, 170, 186 }, { 81, 85, 97, 101, 102, 105, 106, 165, 166, 169, 170, 182, 186 }, { 81, 85, 86, 97, 101, 102, 106, 165, 166, 170, 182, 186 }, { 81, 85, 86, 98, 101, 102, 106, 166, 167, 170, 171, 182, 186, 187 }, { 84, 85, 100, 101, 102, 105, 106, 165, 166, 169, 170, 185, 186 }, { 85, 101, 102, 105, 106, 165, 166, 169, 170, 186 }, { 85, 86, 101, 102, 105, 106, 165, 166, 169, 170, 186 }, { 85, 86, 101, 102, 106, 166, 170, 171, 186, 187 }, { 84, 85, 89, 100, 101, 105, 106, 165, 169, 170, 185, 186 }, { 85, 89, 101, 102, 105, 106, 165, 166, 169, 170, 186 }, { 21, 85, 86, 89, 90, 101, 102, 105, 106, 165, 166, 169, 170, 186 }, { 21, 85, 86, 90, 101, 102, 105, 106, 166, 170, 171, 186, 187 }, { 84, 85, 89, 101, 104, 105, 106, 169, 170, 173, 174, 185, 186, 190 }, { 85, 89, 101, 105, 106, 169, 170, 174, 186, 190 }, { 21, 85, 89, 90, 101, 102, 105, 106, 169, 170, 174, 186, 190 }, { 85, 86, 89, 90, 101, 102, 105, 106, 170, 171, 174, 186, 191 }, { 64, 65, 68, 69, 80, 81, 84, 85, 149, 150, 153, 154, 165, 166, 169, 170 }, { 65, 69, 81, 85, 86, 149, 150, 153, 154, 165, 166, 170 }, { 65, 69, 81, 85, 86, 149, 150, 154, 166, 170 }, { 65, 69, 70, 81, 82, 85, 86, 149, 150, 151, 154, 166, 170, 171 }, { 68, 69, 84, 85, 89, 149, 150, 153, 154, 165, 169, 170 }, { 69, 85, 86, 89, 90, 149, 150, 153, 154, 165, 166, 169, 170, 234 }, { 69, 85, 86, 89, 90, 149, 150, 153, 154, 166, 170 }, { 69, 70, 85, 86, 90, 149, 150, 154, 155, 166, 170, 171 }, { 68, 69, 84, 85, 89, 149, 153, 154, 169, 170 }, { 69, 85, 86, 89, 90, 149, 150, 153, 154, 169, 170 }, { 69, 85, 86, 89, 90, 149, 150, 153, 154, 170 }, { 69, 70, 85, 86, 89, 90, 149, 150, 153, 154, 155, 170, 171 }, { 68, 69, 73, 84, 85, 88, 89, 149, 153, 154, 157, 169, 170, 174 }, { 69, 73, 85, 89, 90, 149, 153, 154, 158, 169, 170, 174 }, { 69, 73, 85, 86, 89, 90, 149, 150, 153, 154, 158, 170, 174 }, { 69, 85, 86, 89, 90, 106, 150, 153, 154, 155, 158, 170, 171, 174, 175 }, { 80, 81, 84, 85, 101, 149, 150, 153, 165, 166, 169, 170 }, { 81, 85, 86, 101, 102, 149, 150, 153, 154, 165, 166, 169, 170, 234 }, { 81, 85, 86, 101, 102, 149, 150, 154, 165, 166, 170 }, { 81, 82, 85, 86, 102, 149, 150, 154, 166, 167, 170, 171 }, { 84, 85, 89, 101, 105, 149, 150, 153, 154, 165, 166, 169, 170, 234 }, { 69, 81, 84, 85, 86, 89, 101, 149, 150, 153, 154, 165, 166, 169, 170, 234 }, { 69, 81, 85, 86, 89, 90, 101, 102, 106, 149, 150, 153, 154, 165, 166, 169, 170, 171, 234 }, { 85, 86, 90, 102, 106, 149, 150, 154, 166, 170, 171 }, { 84, 85, 89, 101, 105, 149, 153, 154, 165, 169, 170 }, { 69, 84, 85, 86, 89, 90, 101, 105, 106, 149, 150, 153, 154, 165, 166, 169, 170, 174, 234 }, { 69, 85, 86, 89, 90, 106, 149, 150, 153, 154, 166, 169, 170 }, { 69, 85, 86, 89, 90, 102, 106, 149, 150, 153, 154, 166, 170, 171 }, { 84, 85, 88, 89, 105, 149, 153, 154, 169, 170, 173, 174 }, { 85, 89, 90, 105, 106, 149, 153, 154, 169, 170, 174 }, { 69, 85, 86, 89, 90, 105, 106, 149, 150, 153, 154, 169, 170, 174 }, { 69, 85, 86, 89, 90, 106, 150, 153, 154, 170, 171, 174, 175 }, { 80, 81, 84, 85, 101, 149, 165, 166, 169, 170 }, { 81, 85, 86, 101, 102, 149, 150, 165, 166, 169, 170 }, { 81, 85, 86, 101, 102, 149, 150, 165, 166, 170 }, { 81, 82, 85, 86, 101, 102, 149, 150, 165, 166, 167, 170, 171 }, { 84, 85, 89, 101, 105, 149, 153, 165, 166, 169, 170 }, { 81, 84, 85, 86, 89, 101, 102, 105, 106, 149, 150, 153, 154, 165, 166, 169, 170, 186, 234 }, { 81, 85, 86, 101, 102, 106, 149, 150, 154, 165, 166, 169, 170 }, { 81, 85, 86, 90, 101, 102, 106, 149, 150, 154, 165, 166, 170, 171 }, { 84, 85, 89, 101, 105, 149, 153, 165, 169, 170 }, { 84, 85, 89, 101, 105, 106, 149, 153, 154, 165, 166, 169, 170 }, { 85, 86, 89, 90, 101, 102, 105, 106, 149, 150, 153, 154, 165, 166, 169, 170 }, { 85, 86, 89, 90, 101, 102, 106, 149, 150, 154, 166, 169, 170, 171 }, { 84, 85, 88, 89, 101, 105, 149, 153, 165, 169, 170, 173, 174 }, { 84, 85, 89, 90, 101, 105, 106, 149, 153, 154, 165, 169, 170, 174 }, { 85, 86, 89, 90, 101, 105, 106, 149, 153, 154, 166, 169, 170, 174 }, { 85, 86, 89, 90, 102, 105, 106, 150, 153, 154, 166, 169, 170, 171, 174, 175 }, { 80, 81, 84, 85, 97, 100, 101, 149, 165, 166, 169, 170, 181, 186 }, { 81, 85, 97, 101, 102, 149, 165, 166, 169, 170, 182, 186 }, { 81, 85, 86, 97, 101, 102, 149, 150, 165, 166, 170, 182, 186 }, { 81, 85, 86, 101, 102, 106, 150, 165, 166, 167, 170, 171, 182, 186, 187 }, { 84, 85, 100, 101, 105, 149, 165, 166, 169, 170, 185, 186 }, { 85, 101, 102, 105, 106, 149, 165, 166, 169, 170, 186 }, { 81, 85, 86, 101, 102, 105, 106, 149, 150, 165, 166, 169, 170, 186 }, { 81, 85, 86, 101, 102, 106, 150, 165, 166, 170, 171, 186, 187 }, { 84, 85, 89, 100, 101, 105, 149, 153, 165, 169, 170, 185, 186 }, { 84, 85, 89, 101, 102, 105, 106, 149, 153, 165, 166, 169, 170, 186 }, { 85, 86, 89, 101, 102, 105, 106, 149, 154, 165, 166, 169, 170, 186 }, { 85, 86, 90, 101, 102, 105, 106, 150, 154, 165, 166, 169, 170, 171, 186, 187 }, { 84, 85, 89, 101, 105, 106, 153, 165, 169, 170, 173, 174, 185, 186, 190 }, { 84, 85, 89, 101, 105, 106, 153, 165, 169, 170, 174, 186, 190 }, { 85, 89, 90, 101, 102, 105, 106, 153, 154, 165, 166, 169, 170, 174, 186, 190 }, { 85, 86, 89, 90, 101, 102, 105, 106, 154, 166, 169, 170, 171, 174, 186 }, { 64, 69, 81, 84, 85, 133, 145, 148, 149, 150, 153, 154, 165, 166, 169, 170, 234 }, { 65, 69, 81, 85, 86, 133, 145, 149, 150, 153, 154, 165, 166, 170, 234 }, { 65, 69, 81, 85, 86, 133, 145, 149, 150, 154, 166, 170, 214, 234 }, { 65, 69, 81, 85, 86, 134, 146, 149, 150, 151, 154, 166, 170, 171, 214, 234, 235 }, { 68, 69, 84, 85, 89, 133, 148, 149, 150, 153, 154, 165, 169, 170, 234 }, { 69, 85, 133, 149, 150, 153, 154, 165, 166, 169, 170, 218, 234 }, { 69, 85, 86, 133, 149, 150, 153, 154, 166, 170, 218, 234 }, { 69, 85, 86, 134, 149, 150, 154, 155, 166, 170, 171, 218, 234, 235 }, { 68, 69, 84, 85, 89, 133, 148, 149, 153, 154, 169, 170, 217, 234 }, { 69, 85, 89, 133, 149, 150, 153, 154, 169, 170, 218, 234 }, { 69, 85, 86, 89, 90, 133, 149, 150, 153, 154, 170, 218, 234 }, { 69, 85, 86, 90, 149, 150, 153, 154, 155, 166, 170, 171, 218, 234, 235 }, { 68, 69, 84, 85, 89, 137, 149, 152, 153, 154, 157, 169, 170, 174, 217, 234, 238 }, { 69, 85, 89, 137, 149, 153, 154, 158, 169, 170, 174, 218, 234, 238 }, { 69, 85, 89, 90, 149, 150, 153, 154, 158, 169, 170, 174, 218, 234, 238 }, { 69, 85, 86, 89, 90, 149, 150, 153, 154, 155, 158, 170, 171, 174, 218, 234, 239 }, { 80, 81, 84, 85, 101, 145, 148, 149, 150, 153, 165, 166, 169, 170, 234 }, { 81, 85, 145, 149, 150, 153, 154, 165, 166, 169, 170, 230, 234 }, { 81, 85, 86, 145, 149, 150, 154, 165, 166, 170, 230, 234 }, { 81, 85, 86, 146, 149, 150, 154, 166, 167, 170, 171, 230, 234, 235 }, { 84, 85, 148, 149, 150, 153, 154, 165, 166, 169, 170, 233, 234 }, { 85, 149, 150, 153, 154, 165, 166, 169, 170, 234 }, { 85, 86, 149, 150, 153, 154, 165, 166, 169, 170, 234 }, { 85, 86, 149, 150, 154, 166, 170, 171, 234, 235 }, { 84, 85, 89, 148, 149, 153, 154, 165, 169, 170, 233, 234 }, { 85, 89, 149, 150, 153, 154, 165, 166, 169, 170, 234 }, { 69, 85, 86, 89, 90, 149, 150, 153, 154, 165, 166, 169, 170, 234 }, { 69, 85, 86, 90, 149, 150, 153, 154, 166, 170, 171, 234, 235 }, { 84, 85, 89, 149, 152, 153, 154, 169, 170, 173, 174, 233, 234, 238 }, { 85, 89, 149, 153, 154, 169, 170, 174, 234, 238 }, { 69, 85, 89, 90, 149, 150, 153, 154, 169, 170, 174, 234, 238 }, { 85, 86, 89, 90, 149, 150, 153, 154, 170, 171, 174, 234, 239 }, { 80, 81, 84, 85, 101, 145, 148, 149, 165, 166, 169, 170, 229, 234 }, { 81, 85, 101, 145, 149, 150, 165, 166, 169, 170, 230, 234 }, { 81, 85, 86, 101, 102, 145, 149, 150, 165, 166, 170, 230, 234 }, { 81, 85, 86, 102, 149, 150, 154, 165, 166, 167, 170, 171, 230, 234, 235 }, { 84, 85, 101, 148, 149, 153, 165, 166, 169, 170, 233, 234 }, { 85, 101, 149, 150, 153, 154, 165, 166, 169, 170, 234 }, { 81, 85, 86, 101, 102, 149, 150, 153, 154, 165, 166, 169, 170, 234 }, { 81, 85, 86, 102, 149, 150, 154, 165, 166, 170, 171, 234, 235 }, { 84, 85, 89, 101, 105, 148, 149, 153, 165, 169, 170, 233, 234 }, { 84, 85, 89, 101, 105, 149, 150, 153, 154, 165, 166, 169, 170, 234 }, { 85, 86, 89, 101, 106, 149, 150, 153, 154, 165, 166, 169, 170, 234 }, { 85, 86, 90, 102, 106, 149, 150, 153, 154, 165, 166, 169, 170, 171, 234, 235 }, { 84, 85, 89, 105, 149, 153, 154, 165, 169, 170, 173, 174, 233, 234, 238 }, { 84, 85, 89, 105, 149, 153, 154, 165, 169, 170, 174, 234, 238 }, { 85, 89, 90, 105, 106, 149, 150, 153, 154, 165, 166, 169, 170, 174, 234, 238 }, { 85, 86, 89, 90, 106, 149, 150, 153, 154, 166, 169, 170, 171, 174, 234 }, { 80, 81, 84, 85, 101, 149, 161, 164, 165, 166, 169, 170, 181, 186, 229, 234, 250 }, { 81, 85, 101, 149, 161, 165, 166, 169, 170, 182, 186, 230, 234, 250 }, { 81, 85, 101, 102, 149, 150, 165, 166, 169, 170, 182, 186, 230, 234, 250 }, { 81, 85, 86, 101, 102, 149, 150, 165, 166, 167, 170, 171, 182, 186, 230, 234, 251 }, { 84, 85, 101, 149, 164, 165, 166, 169, 170, 185, 186, 233, 234, 250 }, { 85, 101, 149, 165, 166, 169, 170, 186, 234, 250 }, { 81, 85, 101, 102, 149, 150, 165, 166, 169, 170, 186, 234, 250 }, { 85, 86, 101, 102, 149, 150, 165, 166, 170, 171, 186, 234, 251 }, { 84, 85, 101, 105, 149, 153, 165, 166, 169, 170, 185, 186, 233, 234, 250 }, { 84, 85, 101, 105, 149, 153, 165, 166, 169, 170, 186, 234, 250 }, { 85, 101, 102, 105, 106, 149, 150, 153, 154, 165, 166, 169, 170, 186, 234, 250 }, { 85, 86, 101, 102, 106, 149, 150, 154, 165, 166, 169, 170, 171, 186, 234 }, { 84, 85, 89, 101, 105, 149, 153, 165, 169, 170, 173, 174, 185, 186, 233, 234, 254 }, { 85, 89, 101, 105, 149, 153, 165, 169, 170, 174, 186, 234, 254 }, { 85, 89, 101, 105, 106, 149, 153, 154, 165, 166, 169, 170, 174, 186, 234 }, { 85, 86, 89, 90, 101, 102, 105, 106, 149, 150, 153, 154, 165, 166, 169, 170, 171, 174, 186, 234 } };
        final LatticeVertex4D[] latticeVerticesByCode = new LatticeVertex4D[256];
        for (int i2 = 0; i2 < 256; ++i2) {
            final int cx = (i2 >> 0 & 0x3) - 1;
            final int cy = (i2 >> 2 & 0x3) - 1;
            final int cz = (i2 >> 4 & 0x3) - 1;
            final int cw = (i2 >> 6 & 0x3) - 1;
            latticeVerticesByCode[i2] = new LatticeVertex4D(cx, cy, cz, cw);
        }
        int nLatticeVerticesTotal = 0;
        for (int i3 = 0; i3 < 256; ++i3) {
            nLatticeVerticesTotal += lookup4DVertexCodes[i3].length;
        }
        OpenSimplexNoise.LOOKUP_4D_A = new int[256];
        OpenSimplexNoise.LOOKUP_4D_B = new LatticeVertex4D[nLatticeVerticesTotal];
        int i3 = 0;
        int j3 = 0;
        while (i3 < 256) {
            OpenSimplexNoise.LOOKUP_4D_A[i3] = (j3 | j3 + lookup4DVertexCodes[i3].length << 16);
            for (int k2 = 0; k2 < lookup4DVertexCodes[i3].length; ++k2) {
                OpenSimplexNoise.LOOKUP_4D_B[j3++] = latticeVerticesByCode[lookup4DVertexCodes[i3][k2]];
            }
            ++i3;
        }
    }

    private static class LatticeVertex4D
    {
        public final float dx;
        public final float dy;
        public final float dz;
        public final float dw;
        public final long xsvp;
        public final long ysvp;
        public final long zsvp;
        public final long wsvp;

        public LatticeVertex4D(final int xsv, final int ysv, final int zsv, final int wsv) {
            this.xsvp = xsv * 5910200641878280303L;
            this.ysvp = ysv * 6452764530575939509L;
            this.zsvp = zsv * 6614699811220273867L;
            this.wsvp = wsv * 6254464313819354443L;
            final float ssv = (xsv + ysv + zsv + wsv) * -0.1381966f;
            this.dx = -xsv - ssv;
            this.dy = -ysv - ssv;
            this.dz = -zsv - ssv;
            this.dw = -wsv - ssv;
        }
    }
}