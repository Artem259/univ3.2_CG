import point_localization as pl


vertices_coords = [(5, 0), (0, 2), (10, 4), (2, 8), (6, 10)]
edges_list = [(0, 1), (0, 2), (1, 3), (2, 3), (2, 4), (3, 4)]
point = (4, 5)
# point = (7, 6)

# vertices_coords = [(0, 20), (15, 14), (1, 10), (7, 8), (20, 3), (3, 0)]
# edges_list = [(0, 1), (0, 2), (1, 2), (1, 3), (1, 5), (1, 4), (2, 3), (3, 5), (4, 5)]
# point = (6, 9)


if __name__ == '__main__':
    graph, tree = pl.preprocessing(vertices_coords, edges_list)
    loc = pl.point_localization(graph, tree, point, visualize=True)
    print(*loc, sep="\n")
    ...
