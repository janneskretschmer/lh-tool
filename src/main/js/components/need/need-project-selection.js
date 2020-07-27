import React from 'react';
import { NeedsContext } from '../../providers/needs-provider';
import { Select, MenuItem, CircularProgress } from '@material-ui/core';

const NeedProjectSelection = props => (
    <NeedsContext.Consumer>
        {needsState => (<>
            Projekt: {
                needsState.projects && needsState.selectedProjectId ? (
                    <Select
                        disabled={needsState.getSelectedProject().loadingMonthData}
                        value={needsState.selectedProjectId}
                        onChange={event => needsState.selectProject(event.target.value)}
                    >
                        {[...needsState.projects.values()].map(cachedProject => (
                            <MenuItem key={cachedProject.id} value={cachedProject.id}>{cachedProject.name}</MenuItem>
                        ))}
                    </Select>
                ) : (
                        <CircularProgress size={15} />
                    )
            }
        </>)}
    </NeedsContext.Consumer>
);
export default NeedProjectSelection;