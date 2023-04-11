from enum import IntEnum
from matplotlib import pyplot as plt
from matplotlib import patches as patches


class Dim(IntEnum):
    VERTICAL = 0
    HORIZONTAL = 1


class TreeNode:
    def __init__(self):
        self.left = None
        self.right = None
        self.point_index = None
        self.point = None
        self.line_dim = None


def _show_plot(points, x_range, y_range):
    fig, ax = plt.subplots(num="Visualization")

    w = x_range[1] - x_range[0]
    h = y_range[1] - y_range[0]
    rect = patches.Rectangle((x_range[0], y_range[0]), w, h)
    ax.add_patch(rect)

    x, y = zip(*points)
    plt.scatter(x, y, color="blue")

    labels = list(range(len(points)))
    for i, txt in enumerate(labels):
        ax.annotate(txt, (x[i], y[i]), xytext=(x[i]+0.1, y[i]+0.1))

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


def _m(node):
    return node.point[node.line_dim]


def _is_inside_rect(point, x_range, y_range):
    return (x_range[0] <= point[0] <= x_range[1]) and (y_range[0] <= point[1] <= y_range[1])


def _preprocessing(points, dim_points, non_dim_points, dim):
    if not dim_points:
        return None
    m_index = (len(dim_points) - 1) // 2
    m = dim_points[m_index]

    left_dim_points, right_dim_points = dim_points[:m_index], dim_points[m_index + 1:]
    left_non_dim_points, right_non_dim_points = _sorted_list_split(left_dim_points, right_dim_points, non_dim_points)
    next_dim = Dim.HORIZONTAL if dim == Dim.VERTICAL else Dim.VERTICAL

    node = TreeNode()
    node.point_index = m
    node.point = points[m]
    node.line_dim = dim
    node.left = _preprocessing(points, left_non_dim_points, left_dim_points, next_dim)
    node.right = _preprocessing(points, right_non_dim_points, right_dim_points, next_dim)
    return node


def preprocessing(points):
    x = y = list(range(len(points)))
    x = sorted(x, key=lambda i: points[i][0])
    y = sorted(y, key=lambda i: points[i][1])
    return _preprocessing(points, x, y, Dim.VERTICAL)


def _range_search(node, x_range, y_range, res):
    left, right = x_range if node.line_dim == Dim.VERTICAL else y_range
    m = _m(node)
    if left <= m <= right:
        if _is_inside_rect(node.point, x_range, y_range):
            res.append([node.point_index, node.point])
    if node.left and left < m:
        _range_search(node.left, x_range, y_range, res)
    if node.right and m < right:
        _range_search(node.right, x_range, y_range, res)


def range_search(tree, x_range, y_range, visualize_points=None):
    if visualize_points is not None:
        _show_plot(visualize_points, x_range, y_range)
    res = []
    _range_search(tree, x_range, y_range, res)
    return res
