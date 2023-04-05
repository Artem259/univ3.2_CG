import functools
import networkx as nx
import numpy as np
import matplotlib.pyplot as plt
from statistics import median_low


class TreeNode:
    def __init__(self):
        self.left = None
        self.right = None
        self.w = None
        self.data = None


def _show_plot(graph, point=None):
    pos = graph.nodes(True)
    pos = {x[0]: (x[1][0], x[1][1]) for x in pos}
    fig, ax = plt.subplots(num="Visualization")
    nx.draw(graph, node_size=400, pos=pos)  # draw nodes and edges
    nx.draw_networkx_labels(graph, pos=pos)  # draw node labels
    plt.axis("on")
    ax.tick_params(left=True, bottom=True, labelleft=True, labelbottom=True)
    if point is not None:
        plt.scatter(point[0], point[1], color="red")
    plt.show()


def _calculate_x(p1, p2, y):
    return -((p2[0]-p1[0]) * (p2[1]-y) / (p2[1]-p1[1]) - p2[0])


def point_to_line(point, p1, p2):
    return -np.sign((p2[0] - p1[0]) * (point[1] - p1[1]) - (p2[1] - p1[1]) * (point[0] - p1[0]))


def _edges_comp(e1, e2):
    e1_p1, e1_p2 = e1[1][0], e1[1][1]
    e2_p1, e2_p2 = e2[1][0], e2[1][1]
    if not max(e1_p1[1], e2_p1[1]) < min(e1_p2[1], e2_p2[1]):
        return 1
    if e1_p1[1] > e2_p1[1]:
        return (-1)*_edges_comp(e2, e1)
    y = (e2_p1[1] + min(e1_p2[1], e2_p2[1])) / 2
    x1 = _calculate_x(e1_p1, e1_p2, y)
    x2 = _calculate_x(e2_p1, e2_p2, y)
    return -1 if x1 < x2 else 1


def balance_w(node):
    if node is None:
        raise Exception("Exception in balance_w")

    def get_w(n): return 0 if (n is None) else n.w
    if type(node.data) is tuple:
        node.w = get_w(node.left) + get_w(node.right)
    else:
        node.w = get_w(node.left) + get_w(node.right) + 1


def create_balanced_tree(u):
    if not u:
        return None
    mid = len(u) // 2
    u[mid].left = create_balanced_tree(u[:mid])
    u[mid].right = create_balanced_tree(u[mid+1:])
    balance_w(u[mid])
    return u[mid]


def _balance(u):
    w_u = sum(i.w for i in u)
    if w_u == 0:
        return create_balanced_tree(u)
    curr_sum = 0
    r = 0
    for curr_u in u:
        curr_sum += curr_u.w
        if curr_sum >= w_u / 2:
            break
        r += 1
    # balance e(r-1) t(r) e(r) balance
    # ..r-2]   r-1    r    r+1  [r+2..
    #    0      1     2     3     4
    seq = [None]*5
    if r > 1:
        seq[0] = _balance(u[:r-1])
    if r > 0:
        seq[1] = u[r-1]
    seq[2] = u[r]
    if r+1 < len(u):
        seq[3] = u[r+1]
    if r+2 < len(u):
        seq[4] = _balance(u[r+2:])

    if seq[1] is None and seq[3] is None:
        root = seq[2]
    elif seq[1] is None:
        root = seq[3]
        root.left = seq[2]
        root.right = seq[4]
    elif seq[3] is None:
        root = seq[1]
        root.left = seq[0]
        root.right = seq[2]
    else:
        tmp = seq[3]
        tmp.left = seq[2]
        tmp.right = seq[4]
        balance_w(tmp)
        # ---
        root = seq[1]
        root.left = seq[0]
        root.right = tmp
    balance_w(root)
    return root


def _trapezoid(g, e, v, i):
    if not v:
        return None
    y_med = median_low([g.nodes[i][1] for i in v])
    e_i, v_i, u_i = [[], []], [[], []], [[], []]
    i_i = [[min(i), y_med], [y_med, max(i)]]
    for curr_e in e:
        coord_0 = g.nodes[curr_e[0]]
        coord_1 = g.nodes[curr_e[1]]
        for i in range(2):
            flag = False
            if i_i[i][0] < coord_0[1] < i_i[i][1]:
                flag = True
                v_i[i].append(curr_e[0])
            if i_i[i][0] < coord_1[1] < i_i[i][1]:
                flag = True
                v_i[i].append(curr_e[1])
            if flag:
                e_i[i].append(curr_e)

            if coord_0[1] <= i_i[i][0] and coord_1[1] >= i_i[i][1]:
                tmp = _trapezoid(g, e_i[i], set(v_i[i]), i_i[i])
                if tmp is not None:
                    u_i[i].append(tmp)
                node = TreeNode()
                node.w = 0
                node.data = curr_e
                u_i[i].append(node)
                e_i[i], v_i[i] = [], []
    for i in range(2):
        tmp = _trapezoid(g, e_i[i], set(v_i[i]), i_i[i])
        if tmp is not None:
            u_i[i].append(tmp)
    node = TreeNode()
    node.left = _balance(u_i[0])
    node.right = _balance(u_i[1])
    node.data = next(x[0] for x in g.nodes(True) if x[1][1] == y_med)
    balance_w(node)
    return node


def preprocessing(vertices_coords, edges_list, visualize=False):
    edges_list = [tuple(sorted(list(e), key=lambda p: vertices_coords[p][1])) for e in edges_list]
    edges_list_t = [(e, (vertices_coords[e[0]], vertices_coords[e[1]])) for e in edges_list]
    edges_list_t = sorted(edges_list_t, key=functools.cmp_to_key(_edges_comp))
    edges_list = [e_t[0] for e_t in edges_list_t]

    graph = nx.empty_graph(len(vertices_coords))
    graph.add_edges_from(edges_list)
    for v in graph:
        graph.nodes[v].update(dict(enumerate(vertices_coords[v])))

    if visualize:
        _show_plot(graph)

    i_0 = [min(vertices_coords, key=lambda x: x[1])[1], max(vertices_coords, key=lambda x: x[1])[1]]
    tree = _trapezoid(graph, edges_list, graph.nodes, i_0)
    return graph, tree


def point_localization(graph, tree, point, visualize=False):
    if visualize:
        _show_plot(graph, point=point)
    path = []
    node = tree
    while node is not None:
        data = node.data
        if type(data) is tuple:
            p1 = graph.nodes[data[0]]
            p2 = graph.nodes[data[1]]
            pos = point_to_line(point, p1, p2)
            if pos == -1:
                d = "left"
                node = node.left
            else:
                d = "right"
                node = node.right
        else:
            y = graph.nodes[data][1]
            if point[1] < y:
                d = "bottom"
                node = node.left
            else:
                d = "top"
                node = node.right
        path.append({"data": data, "dir": d})
    return path
