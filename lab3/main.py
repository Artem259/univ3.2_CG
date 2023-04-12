import nearest_pair as nr


points = [(0, 6), (1, 3.5), (1.5, 6.2), (2, 4.5), (3, 3), (3.2, 5.9), (4, 2), (4.5, 0), (4.1, 4.1)]
# points = [(1, 1), (1.7, 1), (3, 1), (4, 1), (5.5, 1), (6.1, 1), (8, 1)]
# points = [(1, 1), (1.7, 1), (3, 1), (4, 1), (5.5, 0), (6.1, 1), (8, 1)]
# points = [(1, 1), (1, 1.7), (1, 3), (1, 4), (1, 5.5), (1, 6.1), (1, 8)]


if __name__ == '__main__':
    n = nr.nearest_pair(points, visualize=True)
    print(*n, sep="\n")

    d = nr.inf
    for p1_i, p1_v in enumerate(points):
        for p2_v in points[p1_i+1:]:
            d = min(d, nr.distance(p1_v, p2_v))
    print("\n" + str(d))
    ...
