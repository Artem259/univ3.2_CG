import range_search as rs


points = [(0, 7), (1, 2), (2, 5), (3, 6), (4, 0), (5, 10), (6, 1), (7, 3), (10, 9), (11, 4), (12, 8)]
d = ((0.7, 7.3), (2.7, 7.5))
# d = ((0.7, 1.1), (0.7, 2.8))
# d = ((-1.1, 12.3), (0, 10))


if __name__ == '__main__':
    tree = rs.preprocessing(points)
    p = rs.range_search(tree, d[0], d[1], visualize_points=points)
    print(*p, sep="\n")
    ...
