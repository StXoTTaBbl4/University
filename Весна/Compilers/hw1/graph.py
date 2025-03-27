from graphviz import Digraph

# Если надо быстро сгенерить. Так-то лучше делать через программу GraphViz, там не так аляписто получается
def generate_graph(transitions, states, start, stop, frmt="png"):
    dot = Digraph(comment='Finite State Machine')
    dot.attr(rankdir='LR')  # Ориентация графа

    dot.node(start, start+' (Start)')  # Начальное состояние
    dot.node(stop, stop+' (Finish)', shape='doublecircle')  # Конечная

    for state in states:
        if state != start and state != stop:
            dot.node(state, state)

    for src, transitions_dict in transitions.items():
        for symbol, dst in transitions_dict.items():
            dot.edge(src, dst, label=symbol)

    dot.render('finite_state_machine', format=frmt, view=True)