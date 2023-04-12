from math import sqrt, inf
from matplotlib import pyplot as plt


def _show_plot(points, p1, p2):
    fig, ax = plt.subplots(num="Visualization")

    p1_coords, p2_coords = points[p1], points[p2]
    plt.plot([p1_coords[0], p2_coords[0]], [p1_coords[1], p2_coords[1]], zorder=-1)

    x, y = zip(*points)
    plt.scatter(x, y, color="blue")

    labels = list(range(len(points)))
    for i, txt in enumerate(labels):
        ax.annotate(txt, (x[i], y[i]), xytext=(x[i]+0.1, y[i]+0.1))

    plt.gca().set_aspect("equal")
    plt.axis("on")
    plt.ion()
    plt.show()


def _sorted_list_split(left_list, right_list, to_split_list):
    if not to_split_list:
        return [], []
    sep_flags = [2] * (max(to_split_list) + 1)
    for p in left_list:
        sep_flags[p] = 0
    for p in right_list:
        if sep_flags[p] != 2:
            raise Exception("_sorted_list_split")
        sep_flags[p] = 1
    split = [[], [], []]
    for p in to_split_list:
        split[sep_flags[p]].append(p)
    return split[0], split[1]


def distance(p1, p2):
    return sqrt((p1[0] - p2[0])**2 + (p1[1] - p2[1])**2)


def _nearest_pair(points, x_sorted, y_sorted):
    if len(x_sorted) == 1:
        return (None, None), inf
    if len(x_sorted) == 2:
        return tuple(x_sorted), distance(points[x_sorted[0]], points[x_sorted[1]])

    m_index = (len(x_sorted) - 1) // 2
    m = x_sorted[m_index]
    m_x = points[m][0]

    x_left, x_right = x_sorted[:m_index], x_sorted[m_index:]
    y_left, y_right = _sorted_list_split(x_left, x_right, y_sorted)
    left = _nearest_pair(points, x_left, y_left)
    right = _nearest_pair(points, x_right, y_right)
    d = min([left, right], key=lambda x: x[1])

    sides = [x_left, x_right]
    interval_points = [[], []]
    for side_i, side_v in enumerate(sides):
        for p in side_v:
            if abs(points[p][0] - m_x) <= d[1]:
                interval_points[side_i].append(p)
    _, p1 = _sorted_list_split([], interval_points[0], y_left)
    p2, _ = _sorted_list_split(interval_points[1], [], y_right)

    start_i = 0
    for p1_i in p1:
        i = start_i
        while i < len(p2) - 1 and (points[p1_i][1] - points[p2[i]][1]) >= d[1]:
            i += 1
        start_i = i
        while i < len(p2) and abs(points[p1_i][1] - points[p2[i]][1]) <= d[1]:
            d_i = distance(points[p1_i], points[p2[i]])
            if d_i < d[1]:
                d = ((p1_i, p2[i]), d_i)
            i += 1
    return d


def nearest_pair(points, visualize=False):
    x = y = list(range(len(points)))
    x = sorted(x, key=lambda p: points[p][0])
    y = sorted(y, key=lambda p: points[p][1])
    (p1, p2), dst = _nearest_pair(points, x, y)
    if visualize:
        _show_plot(points, p1, p2)
    return (p1, p2), dst
